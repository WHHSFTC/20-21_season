package org.firstinspires.ftc.teamcode.module

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder
import com.acmerobotics.roadrunner.trajectory.constraints.DriveConstraints
import com.acmerobotics.roadrunner.util.Angle
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.switchboard.command.CommandListContext
import org.firstinspires.ftc.teamcode.switchboard.core.*

@Config
abstract class OpMode(val mode: Mode, val al: Alliance = Alliance.BLUE, val setup: SetupPosition = SetupPosition.INNER) : LinearOpMode() {
    val startPose = al at setup
    lateinit var bot: Summum
    lateinit var logger: Logger
    lateinit var config: Configuration

    override fun runOpMode() {
        if (DEBUG)
            telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)

        logger = Logger(telemetry, DEBUG)
        config = Configuration(hardwareMap, logger)

        bot = Summum(logger, config, this, al, setup)
        logger.update()

        initHook()

        bot.setup()
        waitForStart()
        resetStartTime()

        startHook()
        while (!Thread.currentThread().isInterrupted && !isStopRequested)
            bot.update()

        stopHook()
        bot.cleanup()
    }

    open fun initHook() {}
    open fun startHook() {}
    open fun stopHook() {}

    enum class Mode {
        NULL, AUTO, TELE,
    }

    companion object {
        @JvmField var DEBUG = false
    }



    fun CommandListContext.go(startPose: Pose2d, startTangent: Double = startPose.heading, constraints: DriveConstraints = SummumConstants.MECANUM_CONSTRAINTS, b: TrajectoryBuilder.() -> Unit) {
        val traj = TrajectoryBuilder(startPose, startTangent, constraints).apply(b).build()
        task {
            bot.dt.followTrajectoryAsync(traj)
        }
        await { !bot.dt.isBusy }
    }

    fun CommandListContext.go(startPose: Pose2d, reversed: Boolean, constraints: DriveConstraints = SummumConstants.MECANUM_CONSTRAINTS, b: TrajectoryBuilder.() -> Unit) {
        val traj = TrajectoryBuilder(startPose, reversed, constraints).apply(b).build()
        task {
            bot.dt.followTrajectoryAsync(traj)
        }
        await { !bot.dt.isBusy }
    }

    fun CommandListContext.turnTo(target: Double) {
        task {
            bot.dt.turnAsync(Angle.normDelta(target - bot.dt.poseEstimate.heading))
        }
        await { !bot.dt.isBusy }
    }

    fun Vector2d.to(tiles: Double = 1.0)
        = this + Vector2d(0.0, -24.0 * bot.alliance.ysign) * tiles
    fun Vector2d.fro(tiles: Double = 1.0)
        = this + Vector2d(0.0, 24.0 * bot.alliance.ysign) * tiles
    fun Vector2d.aft(tiles: Double = 1.0)
        = this + Vector2d(-24.0, 0.0) * tiles
    fun Vector2d.fore(tiles: Double = 1.0)
        = this + Vector2d(24.0, 0.0) * tiles
    infix fun Vector2d.facing(heading: Double)
        = Pose2d(this, heading)
}
