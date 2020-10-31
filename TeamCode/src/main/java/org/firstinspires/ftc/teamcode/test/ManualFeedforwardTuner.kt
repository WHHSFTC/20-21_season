package org.firstinspires.ftc.teamcode.test

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.kinematics.Kinematics.calculateMotorFeedforward
import com.acmerobotics.roadrunner.profile.MotionProfile
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator.generateSimpleMotionProfile
import com.acmerobotics.roadrunner.profile.MotionState
import com.acmerobotics.roadrunner.util.NanoClock
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.RobotLog
import org.firstinspires.ftc.teamcode.module.CustomMecanumDrive
import org.firstinspires.ftc.teamcode.module.DriveConstants
import org.firstinspires.ftc.teamcode.module.OpMode
import java.util.*

@Config
@TeleOp(group = "tuning")
class ManualFeedforwardTuner: OpMode(OpMode.Mode.TELE) {
    private lateinit var drive: CustomMecanumDrive
    private lateinit var clock: NanoClock
    private var movingForwards: Boolean = false
    private lateinit var activeProfile: MotionProfile
    private var profileStart: Double = 0.0

    internal enum class Mode {
        DRIVER_MODE, TUNING_MODE
    }

    private var mode_: Mode? = null

    override fun onInit() {
        if (DriveConstants.RUN_USING_ENCODER) {
            RobotLog.setGlobalErrorMsg("Feedforward constants usually don't need to be tuned " +
                    "when using the built-in drive motor velocity PID.")
        }
        drive = CustomMecanumDrive(bot)
        mode_ = Mode.TUNING_MODE
        clock = NanoClock.system()
        telemetry.addLine("Ready!")
    }

    override fun onRun() {
        telemetry.clearAll()
        if (isStopRequested) return
        movingForwards = true
        activeProfile = generateProfile(true)
        profileStart = clock.seconds()
    }

    override fun onLoop() {
        telemetry.addData("mode", mode_)
        when (mode_) {
            Mode.TUNING_MODE -> {
                if (gamepad1.x) {
                    mode_ = Mode.DRIVER_MODE
                }

                // calculate and set the motor power
                val profileTime = clock.seconds() - profileStart
                if (profileTime > activeProfile.duration()) {
                    // generate a new profile
                    movingForwards = !movingForwards
                    activeProfile = generateProfile(movingForwards)
                    profileStart = clock.seconds()
                }
                val motionState = activeProfile[profileTime]
                val targetPower = calculateMotorFeedforward(motionState.v, motionState.a, DriveConstants.kV, DriveConstants.kA, DriveConstants.kStatic)
                drive.setDrivePower(Pose2d(targetPower, 0.0, 0.0))
                drive.updatePoseEstimate()
                val (currentVelo) = Objects.requireNonNull(drive.poseVelocity, "poseVelocity() must not be null. Ensure that the getWheelVelocities() method has been overridden in your localizer.")!!

                // update telemetry
                telemetry.addData("targetVelocity", motionState.v)
                telemetry.addData("poseVelocity", currentVelo)
                telemetry.addData("error", currentVelo - motionState.v)
            }
            Mode.DRIVER_MODE -> {
                if (gamepad1.a) {
                    mode_ = Mode.TUNING_MODE
                    movingForwards = true
                    activeProfile = generateProfile(movingForwards)
                    profileStart = clock.seconds()
                }
                drive.setWeightedDrivePower(
                        Pose2d(
                                (-gamepad1.left_stick_y).toDouble(),
                                (-gamepad1.left_stick_x).toDouble(),
                                (-gamepad1.right_stick_x).toDouble()
                        )
                )
            }
        }
    }

    override fun onStop() {}

    companion object {
        @JvmField var DISTANCE = 72.0 // in
        private fun generateProfile(movingForward: Boolean): MotionProfile {
            val start = MotionState(if (movingForward) 0.0 else DISTANCE, 0.0, 0.0, 0.0)
            val goal = MotionState(if (movingForward) DISTANCE else 0.0, 0.0, 0.0, 0.0)
            return generateSimpleMotionProfile(start, goal,
                    DriveConstants.BASE_CONSTRAINTS.maxVel,
                    DriveConstants.BASE_CONSTRAINTS.maxAccel,
                    DriveConstants.BASE_CONSTRAINTS.maxJerk)
        }
    }
}