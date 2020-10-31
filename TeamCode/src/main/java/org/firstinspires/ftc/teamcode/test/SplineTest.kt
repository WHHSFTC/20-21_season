package org.firstinspires.ftc.teamcode.test

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.module.CustomMecanumDrive
import org.firstinspires.ftc.teamcode.module.OpMode

@TeleOp(group = "tuning")
class SplineTest : OpMode(Mode.TELE) {
    private lateinit var drive: CustomMecanumDrive

    override fun onInit() {
        drive = CustomMecanumDrive(bot)
    }

    override fun onRun() {
        val traj = drive.trajectoryBuilder(Pose2d())
                .splineTo(Vector2d(30.0, 30.0), 0.0)
                .build()
        drive.followTrajectory(traj)
        sleep(2000)
        drive.followTrajectory(
                drive.trajectoryBuilder(traj.end(), true)
                        .splineTo(Vector2d(0.0, 0.0), Math.toRadians(180.0))
                        .build()
        )
    }

    override fun onLoop() {
    }

    override fun onStop() {
    }
}
