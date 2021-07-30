package org.firstinspires.ftc.teamcode.auto

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.trajectory.BaseTrajectoryBuilder
import org.firstinspires.ftc.teamcode.module.*
import org.firstinspires.ftc.teamcode.switchboard.command.Command
import org.firstinspires.ftc.teamcode.switchboard.command.makeLinear
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

fun dropWobble(bot: Summum): Command
    = makeLinear {
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
    }

fun wait2Burst(bot: Summum, off: Boolean = true): Command
    = makeLinear {
        awaitUntil(Time.milli(2000)) { !bot.aim.busy && bot.out.enc.velocity > Shooter.MIN_FULL_RPM}

        task {
            bot.feed.burst()
        }

        await { bot.feed.command.done }

        if (off) {
            task {
                bot.out(Shooter.State.OFF)
            }
        }
    }

fun <T : BaseTrajectoryBuilder<T>> BaseTrajectoryBuilder<T>.splineToPose(pose: Pose2d) = splineTo(pose.vec(), pose.heading)

