package org.firstinspires.ftc.teamcode.module

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.gamepad.GamepadEx

abstract class OpMode(val mode: Mode) : LinearOpMode() {
    lateinit var bot: Robot
    override fun runOpMode() {
        telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
        bot = Robot(this)
        onInit()
        bot.log.update()
        waitForStart()
        onRun()
        if (mode == Mode.TELE) {
            while (!Thread.currentThread().isInterrupted && !isStopRequested) {
                onLoop()
                bot.log.update()
            }
        }
        onStop()
        bot.log.update()
    }

    abstract fun onInit()
    abstract fun onRun()
    abstract fun onLoop()
    abstract fun onStop()

    enum class Mode {
        NULL, AUTO, TELE,
    }
}
