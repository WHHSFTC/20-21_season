package org.firstinspires.ftc.teamcode.test

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.util.Angle.norm
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.MovingStatistics
import org.firstinspires.ftc.robotcore.internal.system.Misc
import org.firstinspires.ftc.teamcode.module.CustomMecanumDrive
import org.firstinspires.ftc.teamcode.module.DriveConstants
import org.firstinspires.ftc.teamcode.module.OpMode
import kotlin.math.sqrt

@Config
@TeleOp(group = "drive")
class TrackWidthTuner : OpMode(Mode.TELE) {
    private lateinit var drive: CustomMecanumDrive
    private lateinit var trackWidthStats: MovingStatistics

    override suspend fun onInit() {
        telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
        drive = CustomMecanumDrive(bot)
        // TODO: if you haven't already, set the localizer to something that doesn't depend on
        // drive encoders for computing the heading
        telemetry.addLine("Press play to begin the track width tuner routine")
        telemetry.addLine("Make sure your robot has enough clearance to turn smoothly")
    }

    override suspend fun onRun() {
        telemetry.clearAll()
        telemetry.addLine("Running...")
        trackWidthStats = MovingStatistics(NUM_TRIALS)
        for (i in 0 until NUM_TRIALS) {
            drive.poseEstimate = Pose2d()

            // it is important to handle heading wraparounds
            var headingAccumulator = 0.0
            var lastHeading = 0.0
            drive.turnAsync(Math.toRadians(ANGLE))
            while (!isStopRequested && drive.isBusy) {
                val heading = drive.poseEstimate.heading
                headingAccumulator += norm(heading - lastHeading)
                lastHeading = heading
                drive.update()
            }
            val trackWidth = DriveConstants.TRACK_WIDTH * Math.toRadians(ANGLE) / headingAccumulator
            trackWidthStats.add(trackWidth)
            sleep(DELAY.toLong())
        }
        telemetry.clearAll()
        telemetry.addLine("Tuning complete")
        telemetry.addLine(Misc.formatInvariant("Effective track width = %.2f (SE = %.3f)",
                trackWidthStats.mean,
                trackWidthStats.standardDeviation / sqrt(NUM_TRIALS.toDouble())))
    }

    override suspend fun onLoop() {
        idle()
    }

    override suspend fun onStop() {}

    companion object {
        @JvmField var ANGLE = 180.0 // deg
        @JvmField var NUM_TRIALS = 5
        @JvmField var DELAY = 1000 // ms
    }
}