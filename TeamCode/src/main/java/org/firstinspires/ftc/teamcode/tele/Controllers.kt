package org.firstinspires.ftc.teamcode.tele

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.module.*
import org.firstinspires.ftc.teamcode.switchboard.core.Activity
import org.firstinspires.ftc.teamcode.switchboard.core.Frame
import kotlin.math.*

infix fun Double.max(that: Double) = maxOf(this, that)
fun Gamepad.shift() = right_trigger > .5 || left_trigger > .5

@Config
class Controllers {
    class Luke(val pad: Gamepad, val bot: Summum, /*var fieldCentric: Boolean = false*/): Activity {
        var lastXkey = false
        var rr = false
        fun drivetrain(frame: Frame) {
            if (rr) {
                if (bot.dt.isBusy)
                    return
                rr = false
            }

            val shift = pad.shift()
            val xkey = pad.x
            val strafe = xkey && !lastXkey
            lastXkey = xkey

            if (strafe) {
                val tb = TrajectoryBuilder(startPose = bot.dt.poseEstimate, constraints = SummumConstants.SLOW_CONSTRAINTS)
                when (bot.alliance to shift) {
                    Alliance.RED to true, Alliance.BLUE to false -> tb.strafeRight(STRAFE_DISTANCE)
                    Alliance.RED to false, Alliance.BLUE to true -> tb.strafeLeft(STRAFE_DISTANCE)
                }
                bot.dt.followTrajectoryAsync(tb.build())
                return
            }

            val x = (-pad.left_stick_y).toDouble()
            val y = (-pad.left_stick_x).toDouble()
            var omega = (-pad.right_stick_x).toDouble()

//            if (shift) {
//                val head = Vector2d(-pad.right_stick_y.toDouble(), -pad.right_stick_x.toDouble()).rotated(if (bot.alliance == Alliance.BLUE) -PI / 2.0 else PI / 2.0)
//                val norm = head.norm()
//                val err = (head.angle() - bot.dt.poseEstimate.heading + 4 * PI) % (2 * PI)
//                val sym = if (err > PI) err - 2 * PI else err
//                omega = FLICK_P * sym * if (norm > 0.1) norm else 0.0
//            }

            val linearScalar = (x.absoluteValue max y.absoluteValue).pow(2.0)
            val turtleScalar = if (shift) 3.0 else 1.0

            var twist = Pose2d(
                    x = linearScalar * x,
                    y = linearScalar * y,
                    omega
            ) / turtleScalar

//            if (fieldCentric) {
//                val theta = -bot.dt.poseEstimate.heading + if (bot.alliance == Alliance.BLUE) -PI / 2.0 else PI / 2.0
//                twist = Pose2d(
//                        x = twist.x * cos(theta) - twist.y * sin(theta),
//                        y = twist.x * sin(theta) + twist.y * cos(theta),
//                        twist.heading
//                )
//            }

            bot.dt.botTwist = twist
        }

        fun wobble(frame: Frame) {
            when {
                pad.dpad_up -> bot.wob.elbow(Wobble.ElbowState.CARRY)
                pad.dpad_down -> bot.wob.elbow(Wobble.ElbowState.INTAKE)
                pad.dpad_right -> bot.wob.elbow(Wobble.ElbowState.STORE)
                pad.dpad_left -> bot.wob.quickDrop(frame)
            }

            when {
                pad.left_bumper && !pad.shift() -> bot.wob.claw(Wobble.ClawState.WIDE)
                pad.left_bumper && pad.shift() -> bot.wob.claw(Wobble.ClawState.OPEN)
                pad.right_bumper -> bot.wob.claw(Wobble.ClawState.CLOSED)
            }
        }

        fun intake(frame: Frame) {
            when {
                pad.a -> bot.ink(Intake.Power.OUT)
                pad.b -> bot.ink(Intake.Power.OFF)
                pad.y -> bot.ink(Intake.Power.IN)
            }
        }

        override fun update(frame: Frame) {
            drivetrain(frame)
            wobble(frame)
            intake(frame)
        }
    }

    class Kaylaa(val pad: Gamepad, val bot: Summum): Activity {
        var lastA = false
        var lastB = false
        var lastDeadzone = true
        var lastShoot = false
        var lastBurst = false

        fun flywheel(frame: Frame) {
            val a = pad.a
            val b = pad.b

            when {
                a and !lastA -> {
                    bot.out(bot.aim.height().power)
                }

                b and !lastB -> {
                    bot.out(Shooter.State.OFF)
                }
            }

            lastA = a
            lastB = b
        }

        var lastActive = false

        fun heights(frame: Frame) {
            val leftShift = pad.left_trigger > .5
            val p = -pad.right_stick_y
            val active = leftShift && p.absoluteValue > 0.5
            val rightShift = pad.right_trigger > .5

            if (rightShift) {
                when {
                    //pad.dpad_down -> bot.aim.reset()
                    pad.dpad_right && bot.alliance == Alliance.RED -> bot.aim.height(HeightController.Height.THREE)
                    pad.dpad_left && bot.alliance == Alliance.BLUE -> bot.aim.height(HeightController.Height.THREE)
                    pad.dpad_right && bot.alliance == Alliance.BLUE -> bot.aim.height(HeightController.Height.TWO)
                    pad.dpad_left && bot.alliance == Alliance.RED -> bot.aim.height(HeightController.Height.TWO)
                    pad.dpad_up -> bot.aim.height(HeightController.Height.MID)
                    //pad.dpad_left || pad.dpad_right -> bot.aim.height(HeightController.Height.POWER)
                }

            } else {
                if (active) {
                    bot.aim.power(p.toDouble() * if (p < 0.0) 1.0 else .25)
                } else if (lastActive) {
                    bot.aim.power(0.0)
                }

                when {
                    pad.dpad_down -> bot.feed.height(Indexer.Height.IN)
                    pad.dpad_up -> {
                        bot.feed.height(Indexer.Height.HIGH)
                    }
                    pad.dpad_right || pad.dpad_left -> {
                        bot.feed.height(Indexer.Height.POWER)
                    }
                }
            }

            lastActive = active
        }

        fun shooting(frame: Frame) {
            val shoot = pad.right_bumper
            val burst = pad.left_bumper

            when {
                shoot && !lastShoot -> bot.feed.shoot()
                burst && !lastBurst -> bot.feed.burst()
//                shoot && !lastShoot -> bot.feed.feed(Indexer.Shoot.PRE)
//                burst && !lastBurst -> bot.feed.feed(Indexer.Shoot.POST)
            }

            lastShoot = shoot
            lastBurst = burst
        }

        override fun update(frame: Frame) {
            flywheel(frame)
            heights(frame)
            shooting(frame)
        }
    }

    companion object {
        @JvmField var FLICK_P: Double = 1.0
        @JvmField var STRAFE_DISTANCE: Double = 8.5
    }
}