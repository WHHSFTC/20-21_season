package org.firstinspires.ftc.teamcode.test

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import kotlinx.coroutines.delay
import org.firstinspires.ftc.teamcode.module.*
import org.firstinspires.ftc.teamcode.module.vision.PipelineRunner
import org.firstinspires.ftc.teamcode.module.vision.WobblePipeline
import kotlin.math.PI

@Autonomous
class WobbleFinderTest: OpMode(Mode.NULL) {
    lateinit var find: PipelineRunner<WobblePipeline>
    override suspend fun onInit() {
        find = PipelineRunner<WobblePipeline>(bot)
    }

    override suspend fun onRun() {
        find.halt()
        bot.dt.poseEstimate = Pose2d()
        val camToGoal = find.pipeline.estimate
        val botToCam = Vector2d(9.0, -5.0)
        val botToClaw = Vector2d(13.0, 4.0)
        val intakeToGoal = botToCam + camToGoal - botToClaw
        bot.feed.height(Indexer.Height.IN)
        bot.feed.feed(Indexer.Shoot.PRE)
        bot.wob.elbow(Wobble.ElbowState.INTAKE)
        bot.wob.claw(Wobble.ClawState.OPEN)
        var traj = bot.dt.trajectoryBuilder(Pose2d())
                .splineTo(intakeToGoal + bot.dt.poseEstimate.vec(), Math.toRadians(-20.0) + bot.dt.poseEstimate.heading, constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                .build()
        bot.dt.followTrajectory(traj)

        bot.wob.claw(Wobble.ClawState.CLOSED)
        delay(1000)

        traj = bot.dt.trajectoryBuilder(traj.end(), reversed = true)
                .splineTo(Vector2d(), PI)
                .build()
        bot.dt.followTrajectory(traj)
    }

    override suspend fun onLoop() { }

    override suspend fun onStop() {
        bot.dt.powers = CustomMecanumDrive.Powers()
    }
}