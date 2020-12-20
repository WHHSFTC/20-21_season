package org.firstinspires.ftc.teamcode.test

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.module.CustomMecanumDrive
import org.firstinspires.ftc.teamcode.module.OpMode

@Config
@TeleOp(group = "tuning")
class TurnTest : OpMode(Mode.TELE) {
    private lateinit var drive: CustomMecanumDrive

    override suspend fun onInit() {
        drive = bot.dt
    }

    override suspend fun onRun() {
        drive.turn(Math.toRadians(ANGLE))
    }

    override suspend fun onLoop() {}

    override suspend fun onStop() {}

    companion object {
        @JvmField var ANGLE = 90.0 // deg
    }
}