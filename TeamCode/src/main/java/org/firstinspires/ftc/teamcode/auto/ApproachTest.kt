package org.firstinspires.ftc.teamcode.auto

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.geometry.Vector2d
import org.firstinspires.ftc.teamcode.summum.Drivetrain
import org.firstinspires.ftc.teamcode.summum.Summum
import org.firstinspires.ftc.teamcode.switchboard.core.*
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

@Autonomous
class ApproachTest : LinearOpMode() {
    lateinit var logger: Logger
    lateinit var config: Config
    lateinit var bot: Summum

    override fun runOpMode() {
        logger = Logger(telemetry, displayErr = true)
        config = Config(hardwareMap, logger)
        bot = Summum(logger, config)
        bot.setup()
        logger.update()
        bot.dt.loadFollower.set(Drivetrain.Follower.Approach(Vector2d(48.0, 0.0)))

        waitForStart()
        bot.startTime = Time.now()


        while (opModeIsActive()) {
            val now = Time.now()
            val loopTime = now - bot.startTime!!
            logger.out["Runtime"] = loopTime
            val n = bot.frame.get().n.toDouble()
            logger.out["Loop Cycle Time (ms)"] = loopTime.milliseconds / n
            logger.out["Loop Frequency (hz)"] = n / loopTime.seconds

            bot.update()
        }

        // stop
        bot.cleanup()
        logger.update()
    }
}