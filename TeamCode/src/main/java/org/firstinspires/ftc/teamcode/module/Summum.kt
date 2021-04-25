package org.firstinspires.ftc.teamcode.module

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.switchboard.core.Activity
import org.firstinspires.ftc.teamcode.switchboard.core.*
import org.firstinspires.ftc.teamcode.switchboard.scheduler.*
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

class Summum(
        logger: Logger,
        config: Configuration,
        val opMode: OpMode
) : Robot(logger, config, "Summum") {

    val hwmap: HardwareMap = opMode.hardwareMap

    val feed: Indexer = Indexer(config)
    val wob: Wobble = Wobble(config, logger)
    val loc: CustomLocalizer = CustomLocalizer(config, logger)
    val dt: CustomMecanumDrive = CustomMecanumDrive(this, config)
    val ink: Intake = Intake(this, config)
    val aim: HeightController = HeightController(config, logger)
    //var vis: PipelineRunner = PipelineRunner(this, 640, 480)
    val out: Shooter = Shooter(config, logger)
    val alliance: Alliance = Alliance.BLUE

    override val activities: MutableList<Activity> = mutableListOf(loc, dt, aim, out, wob)
    override val scheduler = bucket(Time.milli(14),
            listOf( // on ones
                    all(*dt.motors.toTypedArray())
            ),

            listOf( // on twos
                    rot(Time.milli(2), feed.feedServo, feed.heightServo),
                    rot(Time.milli(2), wob.claw.servo, wob.elbow.servo)
            ),

            listOf( // on fours
                    out.motor1, aim.motor, ink.motor
            ),

            listOf( // on eights

            )
    )

    init {
        //vis.load(vis.stack)
        //vis.start()
    }

    companion object {
        const val TRACK_WIDTH = 14.5 // TODO
        const val CENTER_OFFSET = 7 // TODO
        const val WIDTH = 17.375
        const val LENGTH = 17.375
    }
}