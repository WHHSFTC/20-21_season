package org.firstinspires.ftc.teamcode.test

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.acmerobotics.roadrunner.trajectory.Trajectory
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import kotlinx.coroutines.delay
import org.firstinspires.ftc.teamcode.module.*

@Autonomous
@Config
class QuickDrop: OpMode(Mode.NULL) {
    lateinit var traj: Trajectory

    override suspend fun onInit() {
        bot.wob.claw(Wobble.ClawState.CLOSED)
        delay(1000)
        bot.wob.elbow(Wobble.ElbowState.CARRY)
        traj = bot.dt.trajectoryBuilder(Pose2d()).back(24.0).build()
    }

    override suspend fun onRun() {
        bot.dt.poseEstimate = Pose2d()
        bot.wob.elbow(Wobble.ElbowState.DROP)
        delay(a.toLong())
        bot.wob.claw(Wobble.ClawState.OPEN)
        delay(b.toLong())
        bot.wob.elbow(Wobble.ElbowState.CARRY)
        delay(c.toLong())
        bot.dt.followTrajectory(traj)
    }

    override suspend fun onLoop() { }

    override suspend fun onStop() {
        bot.dt.powers = CustomMecanumDrive.Powers()
    }

    companion object {
        @JvmField var a = 300
        @JvmField var b = 100
        @JvmField var c = 0
    }
}