package org.firstinspires.ftc.teamcode.summum

import org.firstinspires.ftc.teamcode.geometry.Pose2d
import org.firstinspires.ftc.teamcode.geometry.rad

class SummumLocalizer(val bot: Summum) : DeadWheelLocalizer(
        odos = listOf(
                bot.config.encoders["leftOdo"] to Pose2d(0.0, 8.0, 0.0),
                bot.config.encoders["rightOdo"] to Pose2d(0.0, -8.0, 0.0),
                bot.config.encoders["backOdo"] to Pose2d(-4.0, -8.0, 90.0.rad())
        )
) {
        override fun load() {
                bot.frame.subscribe { update() }
        }
}