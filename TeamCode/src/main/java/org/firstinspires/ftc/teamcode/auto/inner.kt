package org.firstinspires.ftc.teamcode.auto

import org.firstinspires.ftc.teamcode.module.HeightController
import org.firstinspires.ftc.teamcode.module.Indexer
import org.firstinspires.ftc.teamcode.module.OpMode
import org.firstinspires.ftc.teamcode.module.Shooter
import org.firstinspires.ftc.teamcode.module.vision.StackProcessor
import org.firstinspires.ftc.teamcode.switchboard.command.Command
import org.firstinspires.ftc.teamcode.switchboard.command.makeLinear
import kotlin.math.PI

fun OpMode.inner(): Command
    = makeLinear {
    task {
        bot.vis.stack.stop()
        bot.aim.height(HeightController.Height.THREE)
        bot.feed.height(Indexer.Height.HIGH)
        bot.out(Shooter.State.FULL)
    }

    go(startPose, 0.0) {
        splineToPose(al.innerShoot3)
    }

    sub(wait2Burst(bot))

    switch({ bot.vis.stack.height }) {
//        value(StackProcessor.StackPipeline.Height.ZERO) {
//            turnTo(al.direction + PI)
//            await { runtime > 15 }
//            go(Pose2d(al.innerShoot3.vec(), al.direction + PI), al.direction) {
//                lineTo(al.target0.wobTo())
//            }
//            sub(dropWobble(bot))
//            go(Pose2d(al.target0.wobTo(), al.direction + PI), al.direction + PI) {
//                lineTo(al.target0.wobTo().to())
//            }
//        }
        value(StackProcessor.StackPipeline.Height.ZERO) {
            turnTo(0.0)
            await { runtime > 15 }
            go(al.innerShoot3.vec() facing 0.0) {
                splineToConstantHeading(al.innerShoot3.vec().fore(2.0), 0.0)
                splineToConstantHeading(al.innerShoot3.vec().fore(2.0).fro(1.0), PI)
                splineToConstantHeading(al.target0.fro(.25).wobFore(), PI)
            }
            sub(dropWobble(bot))
            go(al.target0.fro(.25).wobFore() facing 0.0) {
                splineToConstantHeading(al.innerShoot3.vec().fore(2.0).fro(1.0), PI)
                splineToConstantHeading(al.innerShoot3.vec().fore(2.0), 0.0)
                splineToConstantHeading(al.innerShoot3.vec().fore(1.0), 0.0)
            }
        }

        value(StackProcessor.StackPipeline.Height.ONE) {
            go(al.innerShoot3) {
                splineTo(al.target1.fore(.25).wobTo(), 0.0)
            }
            turnTo(al.direction + PI)
            sub(dropWobble(bot))
            go(al.target1.fore(.25).wobTo() facing al.direction + PI, PI) {
                splineToConstantHeading(al.target0.to(2.0), PI)
            }
        }

        value(StackProcessor.StackPipeline.Height.FOUR) {
            go(al.innerShoot3) {
                splineTo(al.target4.fore(.25).wobTo(), al.direction)
            }
            turnTo(al.direction + PI)
            sub(dropWobble(bot))
            go(al.target4.fore(.25).wobTo() facing al.direction + PI) {
                splineTo(al.target0.to(2.0), PI)
            }
        }
    }
}