package org.firstinspires.ftc.teamcode.module

import android.os.MessageQueue
import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.acmerobotics.roadrunner.control.PIDCoefficients
import com.acmerobotics.roadrunner.control.PIDFController
import com.acmerobotics.roadrunner.drive.DriveSignal
import com.acmerobotics.roadrunner.drive.MecanumDrive
import com.acmerobotics.roadrunner.followers.HolonomicPIDVAFollower
import com.acmerobotics.roadrunner.followers.TrajectoryFollower
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.profile.MotionProfile
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator.generateSimpleMotionProfile
import com.acmerobotics.roadrunner.profile.MotionState
import com.acmerobotics.roadrunner.trajectory.Trajectory
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder
import com.acmerobotics.roadrunner.trajectory.constraints.DriveConstraints
import com.acmerobotics.roadrunner.trajectory.constraints.MecanumConstraints
import com.acmerobotics.roadrunner.util.NanoClock
import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.hardware.*
import com.qualcomm.robotcore.hardware.DcMotor.RunMode
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior
import org.firstinspires.ftc.teamcode.acme.util.DashboardUtil
import org.firstinspires.ftc.teamcode.acme.util.LynxModuleUtil
import java.util.*
import kotlin.math.abs

/*
* Simple mecanum drive hardware implementation for REV hardware.
*/
@Config
class CustomMecanumDrive(val bot: Robot) : MecanumDrive(DriveConstants.kV, DriveConstants.kA, DriveConstants.kStatic, DriveConstants.TRACK_WIDTH, DriveConstants.TRACK_WIDTH, LATERAL_MULTIPLIER) {
    enum class DriveMode {
        IDLE, TURN, FOLLOW_TRAJECTORY
    }

    private val dashboard: FtcDashboard = FtcDashboard.getInstance()
    private val clock: NanoClock
    private var mode: DriveMode
    private val turnController: PIDFController
    private lateinit var turnProfile: MotionProfile
    private var turnStart = 0.0
    private val constraints: DriveConstraints = DriveConstants.MECANUM_CONSTRAINTS
    private val follower: TrajectoryFollower
    private val poseHistory: LinkedList<Pose2d>
    private val leftFront: DcMotorEx
    private val leftRear: DcMotorEx
    private val rightRear: DcMotorEx
    private val rightFront: DcMotorEx
    private val motors: List<DcMotorEx>
    //private val imu: BNO055IMU
    public val batteryVoltageSensor: VoltageSensor
    private var lastPoseOnTurn: Pose2d? = null
    fun trajectoryBuilder(startPose: Pose2d?): TrajectoryBuilder {
        return TrajectoryBuilder(startPose = startPose!!, constraints = constraints)
    }

    fun trajectoryBuilder(startPose: Pose2d?, reversed: Boolean): TrajectoryBuilder {
        return TrajectoryBuilder(startPose!!, reversed, constraints)
    }

    fun trajectoryBuilder(startPose: Pose2d?, startHeading: Double): TrajectoryBuilder {
        return TrajectoryBuilder(startPose!!, startHeading, constraints)
    }

    fun turnAsync(angle: Double, constraints: DriveConstraints = this.constraints) {
        val heading = poseEstimate.heading
        lastPoseOnTurn = poseEstimate
        turnProfile = generateSimpleMotionProfile(
                MotionState(heading, .0, .0, .0),
                MotionState(heading + angle, .0, .0, .0),
                constraints.maxAngVel,
                constraints.maxAngAccel,
                constraints.maxAngJerk
        )
        turnStart = clock.seconds()
        mode = DriveMode.TURN
    }

    fun turn(angle: Double, constraints: DriveConstraints = this.constraints) {
        turnAsync(angle, constraints)
        waitForIdle()
    }

    fun followTrajectoryAsync(trajectory: Trajectory?) {
        follower.followTrajectory(trajectory!!)
        mode = DriveMode.FOLLOW_TRAJECTORY
    }

    fun followTrajectory(trajectory: Trajectory?) {
        followTrajectoryAsync(trajectory)
        waitForIdle()
    }

    val lastError: Pose2d
        get() = when (mode) {
                    DriveMode.FOLLOW_TRAJECTORY -> follower.lastError
                    DriveMode.TURN -> Pose2d(.0, .0, turnController.lastError)
                    DriveMode.IDLE -> Pose2d()
                }

    fun update() {
        updatePoseEstimate()
        val currentPose = poseEstimate
        val lastError = lastError
        poseHistory.add(currentPose)
        if (POSE_HISTORY_LIMIT > -1 && poseHistory.size > POSE_HISTORY_LIMIT) {
            poseHistory.removeFirst()
        }
        val packet = TelemetryPacket()
        val fieldOverlay = packet.fieldOverlay()
        packet.put("mode", mode)
        packet.put("x", currentPose.x)
        packet.put("y", currentPose.y)
        packet.put("heading", currentPose.heading)
        packet.put("xError", lastError.x)
        packet.put("yError", lastError.y)
        packet.put("headingError", lastError.heading)
        when (mode) {
            DriveMode.IDLE -> { }
            DriveMode.TURN -> {
                val t = clock.seconds() - turnStart
                val targetState = turnProfile[t]
                turnController.targetPosition = targetState.x
                val correction = turnController.update(currentPose.heading)
                val targetOmega = targetState.v
                val targetAlpha = targetState.a
                setDriveSignal(DriveSignal(Pose2d(
                        .0, .0, targetOmega + correction
                ), Pose2d(
                        .0, .0, targetAlpha
                )))
                val newPose = lastPoseOnTurn!!.copy(lastPoseOnTurn!!.x, lastPoseOnTurn!!.y, targetState.x)
                fieldOverlay.setStroke("#4CAF50")
                DashboardUtil.drawRobot(fieldOverlay, newPose)
                if (t >= turnProfile.duration()) {
                    mode = DriveMode.IDLE
                    setDriveSignal(DriveSignal())
                }
            }
            DriveMode.FOLLOW_TRAJECTORY -> {
                setDriveSignal(follower.update(currentPose))
                val trajectory = follower.trajectory
                fieldOverlay.setStrokeWidth(1)
                fieldOverlay.setStroke("#4CAF50")
                DashboardUtil.drawSampledPath(fieldOverlay, trajectory.path)
                val t = follower.elapsedTime()
                DashboardUtil.drawRobot(fieldOverlay, trajectory[t])
                fieldOverlay.setStroke("#3F51B5")
                DashboardUtil.drawPoseHistory(fieldOverlay, poseHistory)
                if (!follower.isFollowing()) {
                    mode = DriveMode.IDLE
                    setDriveSignal(DriveSignal())
                }
            }
        }
        fieldOverlay.setStroke("#3F51B5")
        DashboardUtil.drawRobot(fieldOverlay, currentPose)
        dashboard.sendTelemetryPacket(packet)
    }

    fun waitForIdle() {
        while (!Thread.currentThread().isInterrupted && isBusy && !bot.opMode.isStopRequested /*bot.opMode.opModeIsActive()*/) {
            update()
        }
//        if (!bot.opMode.opModeIsActive()) {
//            mode = DriveMode.IDLE
//            powers = Powers()
//        }
    }

    val isBusy: Boolean
        get() = mode != DriveMode.IDLE

    fun setMode(runMode: RunMode?) {
        for (motor in motors) {
            motor.mode = runMode
        }
    }

    fun setPIDFCoefficients(runMode: RunMode?, coefficients: PIDFCoefficients) {
        val compensatedCoefficients = PIDFCoefficients(
                coefficients.p, coefficients.i, coefficients.d,
                coefficients.f * 12 / batteryVoltageSensor.voltage
        )
        for (motor in motors) {
            motor.setPIDFCoefficients(runMode, compensatedCoefficients)
        }
    }

    fun setWeightedDrivePower(drivePower: Pose2d) {
        var vel = drivePower
        if ((abs(drivePower.x) + abs(drivePower.y)
                        + abs(drivePower.heading)) > 1) {
            // re-normalize the powers according to the weights
            val denom = VX_WEIGHT * abs(drivePower.x) + VY_WEIGHT * abs(drivePower.y) + OMEGA_WEIGHT * abs(drivePower.heading)
            vel = Pose2d(
                    VX_WEIGHT * drivePower.x,
                    VY_WEIGHT * drivePower.y,
                    OMEGA_WEIGHT * drivePower.heading
            ) / denom
        }
        setDrivePower(vel)
    }

    override fun getWheelPositions(): List<Double> {
        val wheelPositions: MutableList<Double> = ArrayList()
        for (motor in motors) {
            wheelPositions.add(DriveConstants.encoderTicksToInches(motor.currentPosition.toDouble()))
        }
        return wheelPositions
    }

    override fun getWheelVelocities(): List<Double>? {
        val wheelVelocities: MutableList<Double> = ArrayList()
        for (motor in motors) {
            wheelVelocities.add(DriveConstants.encoderTicksToInches(motor.velocity))
        }
        return wheelVelocities
    }

    override fun setMotorPowers(frontLeft: Double, rearLeft: Double, rearRight: Double, frontRight: Double) {
        leftFront.power = frontLeft
        leftRear.power = rearLeft
        rightRear.power = rearRight
        rightFront.power = frontRight
    }

    override val rawExternalHeading: Double
        = 0.0

    var powers: Powers = Powers()
        set(value) {
            val (frontLeft, rearLeft, rearRight, frontRight) = value
            bot.log.addData("Powers", "fl: %f, bl: %f, br: %f, fr: %f", frontLeft, rearLeft, rearRight, frontRight)
            this.setMotorPowers(frontLeft = frontLeft, rearLeft = rearLeft, rearRight = rearRight, frontRight = frontRight)
            field = value
        }

    var zeroPowerBehavior: ZeroPowerBehavior = ZeroPowerBehavior.BRAKE
        set(value) {
            for (motor in motors) {
                motor.zeroPowerBehavior = value
            }
            field = value
        }

    var botTwist: Pose2d = Pose2d()
        set(value) {
            this.setWeightedDrivePower(value)
            field = value
        }

    data class Powers(
            var frontLeft: Double = .0,
            var rearLeft: Double = .0,
            var rearRight: Double = .0,
            var frontRight: Double = .0
    )

    @Config
    companion object {
        @JvmField var TRANSLATIONAL_PID = PIDCoefficients(4.0, .0, .0)
        @JvmField var HEADING_PID = PIDCoefficients(8.0, .0, .0)
        @JvmField var LATERAL_MULTIPLIER = 1.0
        @JvmField var VX_WEIGHT = 1.0
        @JvmField var VY_WEIGHT = 1.0
        @JvmField var OMEGA_WEIGHT = 1.0
        @JvmField var POSE_HISTORY_LIMIT = 100
    }

    init {
        dashboard.telemetryTransmissionInterval = 25
        clock = NanoClock.system()
        mode = DriveMode.IDLE
        turnController = PIDFController(HEADING_PID)
        turnController.setInputBounds(0.0, 2 * Math.PI)
        follower = HolonomicPIDVAFollower(TRANSLATIONAL_PID, TRANSLATIONAL_PID, HEADING_PID,
                Pose2d(0.5, 0.5, Math.toRadians(5.0)), 0.5)
        poseHistory = LinkedList()
        LynxModuleUtil.ensureMinimumFirmwareVersion(bot.hwmap)
        batteryVoltageSensor = bot.hwmap.voltageSensor.iterator().next()
        for (module in bot.hwmap.getAll(LynxModule::class.java)) {
            module.bulkCachingMode = LynxModule.BulkCachingMode.AUTO
        }

        // TODO: adjust the names of the following hardware devices to match your configuration
        //imu = bot.hwmap.get(BNO055IMU::class.java, "imu")
        //val parameters = BNO055IMU.Parameters()
        //parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS
        //imu.initialize(parameters)

        // TODO: if your hub is mounted vertically, remap the IMU axes so that the z-axis points
        // upward (normal to the floor) using a command like the following:
        // BNO055IMUUtil.remapAxes(imu, AxesOrder.XYZ, AxesSigns.NPN);
        leftFront = bot.hwmap.get(DcMotorEx::class.java, "motorLF")
        leftRear = bot.hwmap.get(DcMotorEx::class.java, "motorLB")
        rightRear = bot.hwmap.get(DcMotorEx::class.java, "motorRB")
        rightFront = bot.hwmap.get(DcMotorEx::class.java, "motorRF")
        motors = listOf(leftFront, leftRear, rightRear, rightFront)
        for (motor in motors) {
            val motorConfigurationType = motor.motorType.clone()
            motorConfigurationType.achieveableMaxRPMFraction = 1.0
            motor.motorType = motorConfigurationType
        }
        if (DriveConstants.RUN_USING_ENCODER) {
            setMode(RunMode.RUN_USING_ENCODER)
        }
        zeroPowerBehavior = ZeroPowerBehavior.BRAKE
        if (DriveConstants.RUN_USING_ENCODER) {
            setPIDFCoefficients(RunMode.RUN_USING_ENCODER, DriveConstants.MOTOR_VELO_PID)
        }

        // TODO: reverse any motors using DcMotor.setDirection()
        rightRear.direction = DcMotorSimple.Direction.REVERSE
        rightFront.direction = DcMotorSimple.Direction.REVERSE

        localizer = bot.loc
    }
}