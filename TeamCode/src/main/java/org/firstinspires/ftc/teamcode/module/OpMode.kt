package org.firstinspires.ftc.teamcode.module

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.switchboard.core.*

@Config
abstract class OpMode(val mode: Mode) : LinearOpMode() {
    lateinit var bot: Summum
    lateinit var logger: Logger
    lateinit var config: Configuration

    override fun runOpMode() {
        if (DEBUG)
            telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)

        logger = Logger(telemetry, DEBUG)
        config = Configuration(hardwareMap, logger)

        bot = Summum(logger, config, this)
        logger.update()

        initHook()

        bot.setup()
        logger.update()
        waitForStart()

        startHook()
        while (!Thread.currentThread().isInterrupted && !isStopRequested)
            bot.update()

        stopHook()
        bot.cleanup()
    }

    open fun initHook() {}
    open fun startHook() {}
    open fun stopHook() {}

    enum class Mode {
        NULL, AUTO, TELE,
    }

    companion object {
        @JvmField var DEBUG = false
    }
}
