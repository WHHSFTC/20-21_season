package org.firstinspires.ftc.teamcode.summum

import org.firstinspires.ftc.teamcode.geometry.Pose2d
import org.firstinspires.ftc.teamcode.switchboard.core.Config
import org.firstinspires.ftc.teamcode.switchboard.core.Log
import org.firstinspires.ftc.teamcode.switchboard.core.Robot
import org.firstinspires.ftc.teamcode.switchboard.scheduler.Activity

class Summum(log: Log, config: Config) : Robot(log, config, "Summum") {
    val loc = SummumLocalizer(this)
    val dt = Swervey(this)
    val getters = mapOf(
            "PoseEstimate" to loc::poseEstimate
    )
}

class Swervey(val bot: Summum) : Activity {
    var pose: Pose2d = Pose2d(0.0, 0.0, 0.0)
    var velo: Pose2d = Pose2d(0.0, 0.0, 0.0)

    override fun load() {
        bot.loc.pose.subscribe { pose = it }
        bot.loc.velo.subscribe { velo = it }
    }

    override fun update() {
    }
}