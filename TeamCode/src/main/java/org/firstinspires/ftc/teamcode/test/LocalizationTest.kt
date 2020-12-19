package org.firstinspires.ftc.teamcode.test

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.module.CustomMecanumDrive
import org.firstinspires.ftc.teamcode.module.OpMode

@Config
@TeleOp(group = "tuning")
class LocalizationTest : OpMode(Mode.TELE) {
    lateinit var drive: CustomMecanumDrive

    override suspend fun onInit() {
        drive = CustomMecanumDrive(bot)
        drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER)
    }

    override suspend fun onRun() {}

    override suspend fun onLoop() {
        drive.setWeightedDrivePower(
                Pose2d(
                        (-gamepad1.left_stick_y).toDouble(),
                        (-gamepad1.left_stick_x).toDouble(),
                        (-gamepad1.right_stick_x).toDouble()
                )
        )
        drive.update()
        val poseEstimate = drive.poseEstimate
        telemetry.addData("x", poseEstimate.x)
        telemetry.addData("y", poseEstimate.y)
        telemetry.addData("heading", poseEstimate.heading)
    }

    override suspend fun onStop() {}
}