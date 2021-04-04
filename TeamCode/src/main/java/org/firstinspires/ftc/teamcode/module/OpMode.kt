package org.firstinspires.ftc.teamcode.module

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime
import kotlinx.coroutines.runBlocking

abstract class OpMode(val mode: Mode) : LinearOpMode() {
    lateinit var bot: Robot
    lateinit var timer: ElapsedTime
    override fun runOpMode() {
        runBlocking {
            timer = ElapsedTime()
            telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
            bot = Robot(this@OpMode)
            onInit()
            bot.log.update()
            waitForStart()
            timer.reset()
            onRun()
            if (mode == Mode.TELE) {
                while (!Thread.currentThread().isInterrupted && !isStopRequested) {
                    onLoop()
                    telemetry.update()
                }
            }
            telemetry.addData("total time", timer.milliseconds())
            onStop()
            telemetry.update()
        }
    }

    abstract suspend fun onInit()
    abstract suspend fun onRun()
    abstract suspend fun onLoop()
    abstract suspend fun onStop()

    enum class Mode {
        NULL, AUTO, TELE,
    }

    @Config
    companion object {
        @JvmField var DEBUG = false
    }
}
