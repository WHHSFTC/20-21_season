package org.firstinspires.ftc.teamcode.test

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.trajectory.Trajectory
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.module.CustomMecanumDrive
import org.firstinspires.ftc.teamcode.module.OpMode

@Config
@TeleOp(group = "tuning")
class StraightTest : OpMode(Mode.TELE) {
    private lateinit var drive: CustomMecanumDrive
    private lateinit var trajectory: Trajectory

    override suspend fun onInit() {
        drive = CustomMecanumDrive(bot)
        trajectory = drive.trajectoryBuilder(Pose2d())
                .forward(DISTANCE)
                .build()
    }

    override suspend fun onRun() {
        if (isStopRequested) return
        drive.followTrajectory(trajectory)
        val poseEstimate = drive.poseEstimate
        telemetry.addData("finalX", poseEstimate.x)
        telemetry.addData("finalY", poseEstimate.y)
        telemetry.addData("finalHeading", poseEstimate.heading)
    }

    override suspend fun onLoop() {}

    override suspend fun onStop() {}

    companion object {
        @JvmField var DISTANCE = 60.0 // in
    }
}
