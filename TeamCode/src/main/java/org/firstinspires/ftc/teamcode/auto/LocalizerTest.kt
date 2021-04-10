package org.firstinspires.ftc.teamcode.auto

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.summum.Summum
import org.firstinspires.ftc.teamcode.switchboard.core.*
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

@Autonomous
class LocalizerTest : LinearOpMode() {
    lateinit var log: Logger
    lateinit var config: Config
    lateinit var bot: Robot

    override fun runOpMode() {
        log = Logger(telemetry, displayErr = true)
        config = Config(hardwareMap, log)
        bot = Summum(log, config)
        bot.setup()
        log.update()

        waitForStart()
        bot.startTime = Time.now()

        while (opModeIsActive()) {
            val now = Time.now()
            val loopTime = now - bot.startTime!!
            log.out["Runtime"] = loopTime
            val n = bot.frame.get().n.toDouble()
            log.out["Loop Cycle Time (ms)"] = loopTime.milliseconds / n
            log.out["Loop Frequency (hz)"] = n / loopTime.seconds
            bot.update()
        }

        // stop
        bot.cleanup()
        log.update()
    }
}