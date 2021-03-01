package org.firstinspires.ftc.teamcode.test

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.module.Intake
import org.firstinspires.ftc.teamcode.module.OpMode

@Autonomous
class RingTests: OpMode(Mode.AUTO) {
    override suspend fun onInit() {
        bot.vis!!(bot.vis!!.ring)
    }

    override suspend fun onRun() {
        bot.vis!!.halt()
        bot.dt.poseEstimate = Pose2d()
        bot.ink(Intake.Power.IN)
        bot.dt.followTrajectory(bot.dt.trajectoryBuilder(Pose2d()).splineTo(bot.vis!!.ring.absolutes[0], 0.0).build())
        bot.ink(Intake.Power.OFF)
    }

    override suspend fun onLoop() { }

    override suspend fun onStop() { }
}