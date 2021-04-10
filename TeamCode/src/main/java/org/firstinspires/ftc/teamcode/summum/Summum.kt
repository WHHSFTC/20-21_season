package org.firstinspires.ftc.teamcode.summum

import org.firstinspires.ftc.teamcode.switchboard.core.Config
import org.firstinspires.ftc.teamcode.switchboard.core.Logger
import org.firstinspires.ftc.teamcode.switchboard.core.Robot
import org.firstinspires.ftc.teamcode.switchboard.core.Activity
import org.firstinspires.ftc.teamcode.switchboard.scheduler.*
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time.Companion.milli

class Summum(log: Logger, config: Config) : Robot(log, config, "Summum") {
    val loc = Localizer(this)
    //val dt = Drivetrain(this)

    override val activities: List<Activity> = listOf(
            loc,
            //dt,
    )

    override val scheduler: HardwareScheduler = bucket(milli(14),
            listOf(
                    //all(*dt.motors.toTypedArray())
            ),
            //listOf(
                    //rot(milli(2)
                            // intake
                            // wobble claw
                            // wobble arm
                            // left side arm
                            // right side arm
                            // indexer height
                            // feeder
                    //),
                    //rot(milli(4)
                            // lead screw
                            // shooter
                    //),
            //),
    )
}

