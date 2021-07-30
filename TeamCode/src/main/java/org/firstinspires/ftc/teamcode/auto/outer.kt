package org.firstinspires.ftc.teamcode.auto

import org.firstinspires.ftc.teamcode.module.*
import org.firstinspires.ftc.teamcode.module.vision.StackProcessor
import org.firstinspires.ftc.teamcode.switchboard.command.Command
import org.firstinspires.ftc.teamcode.switchboard.command.makeLinear
import kotlin.math.PI

fun OpMode.outer(): Command
    = makeLinear {
    task {
        bot.vis.stack.stop()
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
                strafeTo(al.target0.wobAft())
            }
            sub(dropWobble(bot))
            go(al.target0.wobAft() facing PI, 0.0) {
                splineToConstantHeading(al.target0.wobAft().aft(), PI)
            }
            await { runtime > 26 }
            go(al.target0.wobAft().aft() facing PI, al.direction + PI) {
                splineToConstantHeading(al.target0.to().aft(), 0.0)
                splineToConstantHeading(al.target0.to(1.125), 0.0)
            }

            task {
                bot.ink.hook(Intake.HookPosition.UNLOCKED)
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
            task {
                bot.ink.hook(Intake.HookPosition.UNLOCKED)
            }

            go(startPose, 0.0) {
                splineToPose(al.centerShoot2)
            }

            sub(wait2Burst(bot, off = false))

            task {
                bot.ink(Intake.Power.IN)
                bot.ink.hook(Intake.HookPosition.LOCKED)
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
                splineToPose(al.target1.wobFro() facing 0.0)
            }

            turnTo(al.direction)
            sub(dropWobble(bot))
            go(al.target1.wobFro() facing al.direction) {
                strafeTo(al.target0)
            }
        }

        value(StackProcessor.StackPipeline.Height.FOUR) {
            task {
                bot.ink.hook(Intake.HookPosition.UNLOCKED)
            }

            go(startPose, 0.0) {
                splineToPose(al.centerShoot2)
            }

            sub(wait2Burst(bot, off = false))

            task {
                bot.ink(Intake.Power.IN)
                bot.ink.hook(Intake.HookPosition.LOCKED)
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
                splineTo(al.target4.wobAft(), 0.0)
            }

            turnTo(PI)
            sub(dropWobble(bot))

            go(al.target4.wobAft() facing PI) {
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
}