package org.firstinspires.ftc.teamcode.test

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.module.CustomMecanumDrive
import org.firstinspires.ftc.teamcode.module.OpMode

@Config
@TeleOp(group = "tuning")
class TurnTest : OpMode(Mode.TELE) {
    private lateinit var drive: CustomMecanumDrive

    override fun onInit() {
        drive = bot.dt
    }

    override fun onRun() {
        drive.turn(Math.toRadians(ANGLE))
    }

    override fun onLoop() {}

    override fun onStop() {}

    companion object {
        @JvmField var ANGLE = 90.0 // deg
    }
}