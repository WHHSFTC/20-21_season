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
        do {
            onPeriodic()
            bot.log.update()
        } while (
                !Thread.currentThread().isInterrupted &&
                opModeIsActive() &&
                bot.machine.mode == Mode.TELE
        )
        onStop()
        bot.log.update()
    }

    abstract fun onInit()
    abstract fun onPeriodic()
    abstract fun onStop()

    enum class Mode {
        NULL, AUTO, TELE,
    }
}
