package org.firstinspires.ftc.teamcode.module

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode

abstract class OpMode(val mode: Mode) : LinearOpMode() {
    lateinit var bot: Robot
    val dashboard = FtcDashboard.getInstance()
    override fun runOpMode() {
        telemetry = MultipleTelemetry(telemetry, dashboard.telemetry)
        bot = Robot(this/*emptyMachine()*/)
        onInit()
        bot.log.update()
        waitForStart()
        onRun()
        while (!Thread.currentThread().isInterrupted && !isStopRequested) {
            onLoop()
            bot.log.update()
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
