package org.firstinspires.ftc.teamcode.test

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.module.CustomMecanumDrive
import org.firstinspires.ftc.teamcode.module.OpMode

@Autonomous(group = "drive")
class FollowerPIDTuner : OpMode(Mode.TELE) {
    private lateinit var drive: CustomMecanumDrive
    private lateinit var startPose: Pose2d

    override suspend fun onInit() {
        drive = CustomMecanumDrive(bot)
    }

    override suspend fun onRun() {
        startPose = Pose2d(-DISTANCE / 2, -DISTANCE / 2, 0.0)
        drive.poseEstimate = startPose
    }

    override suspend fun onLoop() {
        val traj = drive.trajectoryBuilder(startPose)
                .forward(DISTANCE)
                .build()
        drive.followTrajectory(traj)
        drive.turn(Math.toRadians(90.0))
        startPose = traj.end().plus(Pose2d(0.0, 0.0, Math.toRadians(90.0)))
    }

    override suspend fun onStop() {}

    companion object {
        @JvmField var DISTANCE = 48.0 // in
    }
}