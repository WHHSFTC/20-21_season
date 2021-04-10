package org.firstinspires.ftc.teamcode.summum

import org.firstinspires.ftc.teamcode.geometry.Pose2d
import org.firstinspires.ftc.teamcode.geometry.rad
import org.firstinspires.ftc.teamcode.switchboard.shapes.Distance

class Localizer(val bot: Summum) : DeadWheelLocalizer(bot.log,
        odos = listOf(
                bot.config.encoders["leftOdo"] to Pose2d(1.71, 7.375, 0.0),
                bot.config.encoders["rightOdo"] to Pose2d(1.71, -7.375, 0.0),
                bot.config.encoders["backOdo"] to Pose2d(-2.41, -7.375, 90.0.rad())
        )
) {
        override fun load() {
                bot.frame.subscribe { update() }
        }

        override fun ticksToDistance(n: Int): Distance = FACTOR * n.toDouble()

        companion object {
                val WHEEL_RADIUS = Distance.mm(30.0)
                val GEAR_RATIO = 1.0
                val TICKS_PER_REV = 8192.0
                val FACTOR get() = WHEEL_RADIUS * 2.0 * Math.PI * GEAR_RATIO * TICKS_PER_REV
        }
}