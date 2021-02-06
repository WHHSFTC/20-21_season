package org.firstinspires.ftc.teamcode.test

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.module.CustomMecanumDrive
import org.firstinspires.ftc.teamcode.module.Indexer
import org.firstinspires.ftc.teamcode.module.Intake
import org.firstinspires.ftc.teamcode.module.OpMode
import org.firstinspires.ftc.teamcode.module.rings.RingFinder
import kotlin.math.cos
import kotlin.math.sin

@Autonomous
class RingFinderTest: OpMode(Mode.TELE) {
    lateinit var find: RingFinder
    override suspend fun onInit() {
        find = RingFinder(bot)
    }

    override suspend fun onRun() {
        find.halt()
        bot.dt.poseEstimate = Pose2d()
        val camToRing = Vector2d(find.pipeline.distance * cos(-find.pipeline.alpha), find.pipeline.distance * sin(-find.pipeline.alpha)).rotated(bot.dt.poseEstimate.heading)
        val botToCam = Vector2d(9.0, -5.0)
        val botToIntake = Vector2d(9.0, 0.0)
        val intakeToRing = botToCam + camToRing - botToIntake
        bot.feed.height(Indexer.Height.IN)
        bot.feed.feed(Indexer.Shoot.PRE)
        bot.ink(Intake.Power.IN)
        var traj = bot.dt.trajectoryBuilder(Pose2d())
                .splineTo(intakeToRing + bot.dt.poseEstimate.vec(), -find.pipeline.alpha)
                .build()
        bot.dt.followTrajectory(traj)
        traj = bot.dt.trajectoryBuilder(traj.end())
                .forward(8.0)
                .build()
        bot.dt.followTrajectory(traj)
        bot.ink(Intake.Power.OFF)
        traj = bot.dt.trajectoryBuilder(traj.end(), reversed = true)
                .splineTo(Vector2d(), 0.0)
                .build()
        bot.dt.followTrajectory(traj)
    }

    override suspend fun onLoop() {
        TODO("Not yet implemented")
    }

    override suspend fun onStop() {
        bot.dt.powers = CustomMecanumDrive.Powers()
    }
}