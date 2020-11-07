package org.firstinspires.ftc.teamcode.auto

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.module.CustomMecanumDrive
import org.firstinspires.ftc.teamcode.module.DriveTrain
import org.firstinspires.ftc.teamcode.module.OpMode
import org.firstinspires.ftc.teamcode.module.VisionPipeline

@Autonomous
class VisionTest: OpMode(Mode.AUTO) {
    override fun onInit() {
        bot.log.addLine("VisionTest Auto")
        bot.log.addLine("Setup with rings in the middle of frame")
        // ...
        bot.log.addLine("Init Done")
        bot.log.update()
    }

    override fun onRun() {
        bot.vis!!.halt()
        bot.log.addData("[vis] height", bot.vis!!.height)
        bot.log.update()
    }

    override fun onLoop() {}

    override fun onStop() {
        bot.dt.powers = CustomMecanumDrive.Powers()
    }
}