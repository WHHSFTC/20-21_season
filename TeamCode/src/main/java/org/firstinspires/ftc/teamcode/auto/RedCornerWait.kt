package org.firstinspires.ftc.teamcode.auto

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.module.*
import org.firstinspires.ftc.teamcode.switchboard.command.makeLinear
import org.firstinspires.ftc.teamcode.switchboard.command.toActivity
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time
import kotlin.math.PI

@Autonomous
class RedCornerWait: OpMode(Mode.TELE, Alliance.BLUE, SetupPosition.OUTER) {
    override fun initHook() {
        bot.wob.elbow(Wobble.ElbowState.STORE)
        bot.wob.claw(Wobble.ClawState.CLOSED)
        bot.ink.hook(Intake.HookPosition.LOCKED)

        val startPose = Pose2d(
            pos = Vector2d(-72.0 + Summum.LENGTH / 2.0, -48.0 - Summum.WIDTH / 2.0),
            heading = 0.0
        )

        bot.dt.poseEstimate = startPose

        bot.prependActivity(
            makeLinear {
                task {
                    bot.ink.hook(Intake.HookPosition.UNLOCKED)
                    bot.aim.height(HeightController.Height.MID)
                    bot.feed.height(Indexer.Height.HIGH)
                    bot.out(Shooter.State.FULL)
                    bot.wob.elbow(Wobble.ElbowState.CARRY)
                }

                await { !bot.aim.busy && bot.out.enc.velocity > Shooter.MIN_FULL_RPM }

                task {
                    bot.feed.burst()
                }

                await { bot.feed.command.done }

                task {
                    bot.out(Shooter.State.OFF)
                }

                delay(Time.seconds(20))

                go(startPose, 0.0) {
                    lineToLinearHeading(Pose2d(0.0, -24.0, PI / 2.0))
                }

                await { !bot.dt.isBusy }

                task {
                    bot.wob.elbow(Wobble.ElbowState.DROP)
                }

                delay(1000)

                task {
                    bot.wob.claw(Wobble.ClawState.OPEN)
                }

                delay(1000)

                task {
                    bot.wob.elbow(Wobble.ElbowState.CARRY)
                }
            }.toActivity()
        )
    }
}