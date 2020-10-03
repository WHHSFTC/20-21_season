package org.firstinspires.ftc.teamcode.module

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.fsm.emptyMachine
import org.firstinspires.ftc.teamcode.module.Robot

abstract class OpMode : LinearOpMode() {
    lateinit var bot: Robot
    override fun runOpMode() {
        bot = Robot(this, emptyMachine())
        onInit()
        bot.log.update()
        waitForStart()
        onRun()
        bot.log.update()
        while (opModeIsActive()) onLoop(); bot.log.update()
        onStop()
        bot.log.update()
    }

    abstract fun onInit()
    abstract fun onRun()
    abstract fun onLoop()
    abstract fun onStop()
}
