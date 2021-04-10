package org.firstinspires.ftc.teamcode.summum

import org.firstinspires.ftc.teamcode.geometry.Pose2d
import org.firstinspires.ftc.teamcode.switchboard.core.Activity

class Drivetrain(val bot: Summum) : Activity {
    val motors = listOf("RF", "LF", "LB", "RB").map { bot.config.motors["motor$it"] }

    override fun load() {
        (bot.loc.pose zip bot.loc.velo).subscribe {
            update(it.first, it.second)
        }
    }

    private fun update(pose: Pose2d, velo: Pose2d) {
        // state machine or just grind
    }

    override fun cleanup() {
        setMotorPowers(0.0, 0.0, 0.0, 0.0)
    }

    fun setMotorPowers(rf: Double, lf: Double, lb: Double, rb: Double) {
        motors.zip(listOf(rf, lf, lb, rb)).forEach { it.first.power = it.second }
    }
}