package org.firstinspires.ftc.teamcode.auto

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.module.*
import org.firstinspires.ftc.teamcode.module.vision.StackProcessor
import org.firstinspires.ftc.teamcode.switchboard.command.makeLinear
import org.firstinspires.ftc.teamcode.switchboard.command.toActivity
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time
import kotlin.math.PI

@Autonomous(name="BEACH BOTS")
class BeachBots: OpMode(Mode.AUTO, Alliance.RED, SetupPosition.INNER) {
    override fun initHook() {
        bot.wob.elbow(Wobble.ElbowState.STORE)
        bot.wob.claw(Wobble.ClawState.CLOSED)
        bot.ink.hook(Intake.HookPosition.LOCKED)

        bot.dt.poseEstimate = startPose

        bot.vis.stack.start()

        bot.prependActivity(
            makeLinear {
                task {
                    bot.vis.stack.stop()
                    bot.ink.hook(Intake.HookPosition.UNLOCKED)
                    bot.aim.height(HeightController.Height.THREE)
                    bot.feed.height(Indexer.Height.HIGH)
                    bot.out(Shooter.State.FULL)
                }

                go(startPose, 0.0) {
                    splineToPose(al.outerShoot2)
                }

                sub(wait2Burst(bot))

                switch({ bot.vis.stack.height }) {
                    value(StackProcessor.StackPipeline.Height.ZERO) {
                        await { runtime > 15 }
                        turnTo(al.direction + PI)
                        go(Pose2d(al.innerShoot3.vec(), al.direction + PI), al.direction) {
                            lineTo(al.target0.to())
                        }
                        sub(dropWobble(bot))
                        go(Pose2d(al.target0.to(), al.direction + PI), al.direction + PI) {
                            lineTo(al.target0.to().to())
                        }
                    }

                    value(StackProcessor.StackPipeline.Height.ONE) {
                        go(al.innerShoot3) {
                            splineTo(al.target1.to(), 0.0)
                        }
                        turnTo(al.direction + PI)
                        sub(dropWobble(bot))
                        go(Pose2d(al.target1.to(), al.direction + PI)) {
                            splineTo(Vector2d(12.0, -12.0), PI)
                        }
                    }

                    value(StackProcessor.StackPipeline.Height.FOUR) {
                        go(al.innerShoot3) {
                            splineTo(al.target4.to(), al.direction)
                        }
                        turnTo(al.direction + PI)
                        sub(dropWobble(bot))
                        go(Pose2d(al.target4.to(), al.direction + PI)) {
                            splineTo(Vector2d(12.0, -12.0), PI)
                        }
                    }
                }

            }.toActivity()
        )
    }
}
