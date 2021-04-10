package org.firstinspires.ftc.teamcode.summum

import org.firstinspires.ftc.teamcode.geometry.Pose2d
import org.firstinspires.ftc.teamcode.switchboard.core.Config
import org.firstinspires.ftc.teamcode.switchboard.core.Log
import org.firstinspires.ftc.teamcode.switchboard.core.Robot
import org.firstinspires.ftc.teamcode.switchboard.core.Activity
import org.firstinspires.ftc.teamcode.switchboard.scheduler.AllScheduler
import org.firstinspires.ftc.teamcode.switchboard.scheduler.BucketScheduler
import org.firstinspires.ftc.teamcode.switchboard.scheduler.HardwareScheduler
import org.firstinspires.ftc.teamcode.switchboard.scheduler.RotatingScheduler
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

class Summum(log: Log, config: Config) : Robot(log, config, "Summum") {
    val loc = SummumLocalizer(this)
    val dt = Swervey(this)
    override val activities: List<Activity> = listOf(
            loc,
            dt,
    )

    override val scheduler: HardwareScheduler = BucketScheduler(Time.milli(16), listOf(
            listOf(AllScheduler(dt.motors)),
            listOf(
                    RotatingScheduler(Time.milli(2), listOf(
                            // intake
                            // wobble claw
                            // wobble arm
                            // left side arm
                            // right side arm
                            // indexer height
                            // feeder
                    )),
                    RotatingScheduler(Time.milli(4), listOf(
                            // lead screw
                            // shooter
                    ))
            )
    ))
}

class Swervey(val bot: Summum) : Activity {
    val motors = listOf("RF", "LF", "LB", "RB").map { bot.config.motors["motor$it"] }
    var pose: Pose2d = Pose2d(0.0, 0.0, 0.0)
    var velo: Pose2d = Pose2d(0.0, 0.0, 0.0)

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