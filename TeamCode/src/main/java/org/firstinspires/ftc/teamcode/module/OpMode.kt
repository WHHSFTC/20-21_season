package org.firstinspires.ftc.teamcode.module

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.gamepad.GamepadEx

abstract class OpMode(val mode: Mode) : LinearOpMode() {
    lateinit var bot: Robot
    override fun runOpMode() {
        runBlocking {
            telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
            bot = Robot(this@OpMode)
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
    }

    abstract suspend fun onInit()
    abstract suspend fun onRun()
    abstract suspend fun onLoop()
    abstract suspend fun onStop()

    enum class Mode {
        NULL, AUTO, TELE,
    }
}
