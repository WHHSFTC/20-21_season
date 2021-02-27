package org.firstinspires.ftc.teamcode.test

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.module.CustomMecanumDrive
import org.firstinspires.ftc.teamcode.module.OpMode

@Autonomous
class VisionTest: OpMode(Mode.AUTO) {
    override suspend fun onInit() {
        bot.log.addLine("VisionTest Auto")
        bot.log.addLine("Setup with rings in the middle of frame")
        // ...
        bot.log.addLine("Init Done")
        bot.log.update()
    }

    override suspend fun onRun() {
        bot.vis!!.halt()
        bot.log.addData("[vis] height", bot.vis!!.stack.height)
        bot.log.update()
    }

    override suspend fun onLoop() {}

    override suspend fun onStop() {
        bot.dt.powers = CustomMecanumDrive.Powers()
    }
}