package org.firstinspires.ftc.teamcode.test

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.module.CustomMecanumDrive
import org.firstinspires.ftc.teamcode.module.OpMode

@TeleOp(group = "tuning")
class TurnTest : OpMode(Mode.TELE) {
    private lateinit var drive: CustomMecanumDrive

    override fun onInit() {
        drive = CustomMecanumDrive(bot)
    }

    override fun onRun() {
        drive.turn(Math.toRadians(ANGLE))
    }

    override fun onLoop() {}

    override fun onStop() {}

    companion object {
        var ANGLE = 90.0 // deg
    }
}