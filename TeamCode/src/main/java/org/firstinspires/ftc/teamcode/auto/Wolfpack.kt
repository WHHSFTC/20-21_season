package org.firstinspires.ftc.teamcode.auto

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.acmerobotics.roadrunner.util.Angle
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.module.*
import org.firstinspires.ftc.teamcode.module.vision.StackProcessor
import org.firstinspires.ftc.teamcode.switchboard.command.makeLinear
import org.firstinspires.ftc.teamcode.switchboard.command.toActivity
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time
import kotlin.math.PI

@Autonomous(name="WOLFPACK MACHINA")
class Wolfpack: OpMode(Mode.AUTO, Alliance.BLUE, SetupPosition.OUTER) {
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
                    bot.aim.height(HeightController.Height.TWO)
                    bot.feed.height(Indexer.Height.HIGH)
                    bot.out(Shooter.State.FULL)
                }

                switch({ bot.vis.stack.height }) {
                    value(StackProcessor.StackPipeline.Height.ZERO) {
                        go(startPose, 0.0) {
                            splineToPose(al.outerShoot2)
                        }

                        sub(wait2Burst(bot))

                        turnTo(PI)
                        go(al.outerShoot2.vec() facing PI) {
                            strafeTo(al.target0.aft(0.875))
                        }
                        go(al.target0.aft(0.875) facing PI) {
                            strafeLeft(6.0)
                        }
                        sub(dropWobble(bot))
                        go(al.target0.aft(0.875).to(al.woffset) facing PI, 0.0) {
                            splineToConstantHeading(al.target0.aft(0.875).to(al.woffset).aft(), PI)
                        }
                        await { runtime > 25 }
                        go(al.target0.aft(0.875).to(al.woffset).aft() facing PI, al.direction + PI) {
                            splineToConstantHeading(al.target0.to().aft(), 0.0)
                            splineToConstantHeading(al.target0.to(1.125), 0.0)
                        }
                    }

//                    value(StackProcessor.StackPipeline.Height.ONE) {
//                        go(al.outerShoot2) {
//                            splineToPose(al.target1.fro(.875).aft(0.25) facing 0.0)
//                        }
//                        turnTo(al.direction)
//                        sub(dropWobble(bot))
//                        task { bot.feed.height(Indexer.Height.IN) }
//                        go(al.target1.fro(.875).aft(.25) facing al.direction) {
//                            splineToPose(al.target0 facing PI)
//                            addDisplacementMarker { bot.ink(Intake.Power.IN) }
//                            //splineToPose(al.stack facing PI + al.ysign * PI/4.0)
//                            splineToPose(al.stack facing PI)
//                            splineToPose(al.stack.aft() facing PI)
//                            addDisplacementMarker { bot.ink(Intake.Power.OFF) }
//                            splineToPose(al.stack.aft().fro(0.5) facing al.direction)
//                            splineToPose(al.outerShoot3)
//                        }
//                        task {
//                            bot.aim.height(HeightController.Height.THREE)
//                            bot.feed.height(Indexer.Height.HIGH)
//                            bot.out(Shooter.State.FULL)
//                        }
//                        sub(wait2Burst(bot))
//                        go(al.outerShoot3, PI) {
//                            splineToPose(al.target0 facing 0.0)
//                        }
//                    }
                    value(StackProcessor.StackPipeline.Height.ONE) {
                        go(startPose, 0.0) {
                            splineToPose(al.centerShoot2)
                        }

                        sub(wait2Burst(bot, off = false))

                        task {
                            bot.ink(Intake.Power.IN)
                            bot.aim.height(HeightController.Height.THREE)
                        }
                        go(al.centerShoot2, constraints = SummumConstants.SLOW_CONSTRAINTS) {
                            splineTo(al.stack, 0.0)
                            splineToPose(al.centerShoot3)
                        }

                        task {
                            bot.feed.height(Indexer.Height.HIGH)
                            bot.ink(Intake.Power.OFF)
                        }

                        sub(wait2Burst(bot, off = true))

                        go(al.centerShoot3) {
                            splineToPose(al.target0 facing 0.0)
                            splineToPose(al.target1.fro(.875).aft(al.woffset) facing 0.0)
                        }

                        turnTo(al.direction)
                        sub(dropWobble(bot))
                        go(Pose2d(al.target1.fro(.875).aft(al.woffset), al.direction)) {
                            strafeTo(al.target0)
                        }
                    }

                    value(StackProcessor.StackPipeline.Height.FOUR) {
                        go(startPose, 0.0) {
                            splineToPose(al.centerShoot2)
                        }

                        sub(wait2Burst(bot, off = false))

                        task {
                            bot.ink(Intake.Power.IN)
                        }
                        go(al.centerShoot2, constraints = SummumConstants.SLOW_CONSTRAINTS) {
                            splineToPose(al.stack.aft(0.375) facing 0.0)
                            splineToPose(al.stack.aft(0.125) facing al.arc)
                        }

                        task {
                            bot.feed.height(Indexer.Height.HIGH)
                        }

                        sub(wait2Burst(bot, off = false))

                        task {
                            bot.aim.height(HeightController.Height.THREE)
                        }

                        go(al.stack facing al.arc, constraints = SummumConstants.SLOW_CONSTRAINTS) {
                            splineToPose(al.centerShoot3)
                        }

                        task {
                            bot.feed.height(Indexer.Height.HIGH)
                            bot.ink(Intake.Power.OFF)
                        }

                        sub(wait2Burst(bot, off = true))

                        go(al.centerShoot3) {
                            splineTo(al.target0, 0.0)
                            splineTo(al.target4.aft(.875).to(al.woffset), 0.0)
                        }

                        turnTo(PI)
                        sub(dropWobble(bot))

                        go(al.target4.aft(.875).to(al.woffset) facing PI) {
                            splineToPose(al.target0 facing PI)
                        }
                    }

//                    value(StackProcessor.StackPipeline.Height.FOUR) {
//                        go(startPose, 0.0) {
//                            splineToPose(al.outerShoot2)
//                        }
//
//                        sub(wait2Burst(bot))
//
//                        go(al.outerShoot3) {
//                            splineToPose(al.target4.aft(.875).to(.25) facing 0.0)
//                        }
//                        turnTo(PI)
//                        sub(dropWobble(bot))
//                        go(al.target4.aft(.875).to(.25) facing PI) {
//                            splineToPose(al.target0 facing PI)
//                        }
//                    }
                }

            }.toActivity()
        )
    }
}
