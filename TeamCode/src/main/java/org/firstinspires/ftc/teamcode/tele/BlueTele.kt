package org.firstinspires.ftc.teamcode.tele

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.module.*
import org.firstinspires.ftc.teamcode.switchboard.core.Activity
import org.firstinspires.ftc.teamcode.switchboard.core.Frame
import kotlin.math.absoluteValue
import kotlin.math.pow

@TeleOp
class BlueTele: OpMode(Mode.TELE) {
    override fun startHook() {
        bot.prependActivity(Andrew(gamepad1))
        bot.prependActivity(Adham(gamepad2))
    }

    inner class Andrew(val pad: Gamepad): Activity {
        fun drivetrain(frame: Frame) {
            val turtle = pad.shift()
            val x = (-pad.left_stick_y).toDouble()
            val y = (-pad.left_stick_x).toDouble()
            val omega = (-pad.right_stick_x).toDouble()

            val linearScalar = (x.absoluteValue max y.absoluteValue).pow(2.0)
            val turtleScalar = if (turtle) 3.0 else 1.0

            val twist = Pose2d(
                    x = linearScalar * x,
                    y = linearScalar * y,
                    omega
            ) / turtleScalar

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
                pad.left_bumper -> bot.wob.claw(Wobble.ClawState.OPEN)
                pad.right_bumper -> bot.wob.claw(Wobble.ClawState.CLOSED)
            }
        }

        fun intake(frame: Frame) {
            when {
                pad.a -> bot.ink(Intake.Power.IN)
                pad.b -> bot.ink(Intake.Power.OFF)
                pad.y -> bot.ink(Intake.Power.OUT)
            }
        }

        override fun update(frame: Frame) {
            drivetrain(frame)
            wobble(frame)
            intake(frame)
        }
    }

    inner class Adham(val pad: Gamepad): Activity {
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

        var lastLeftShift = false

        fun heights(frame: Frame) {
            val leftShift = pad.left_trigger > .5
            val rightShift = pad.right_trigger > .5

            if (rightShift) {
                when {
                    pad.dpad_down -> bot.aim.reset()
                    pad.dpad_up -> bot.aim.height(HeightController.Height.HIGH)
                    pad.dpad_left || pad.dpad_right -> bot.aim.height(HeightController.Height.POWER)
                }

            } else {
                if (leftShift) {
                    val p = -pad.right_stick_y
                    val active = p.absoluteValue > 0.1

                    if (active)
                        bot.aim.power(p.toDouble() / 4.0)
                    else
                        bot.aim.power(0.0)
                } else if (lastLeftShift) {
                    bot.aim.power(0.0)
                }

                when {
                    pad.dpad_up -> bot.feed.height(Indexer.Height.HIGH)
                    pad.dpad_down -> bot.feed.height(Indexer.Height.IN)
                    pad.dpad_right || pad.dpad_left -> bot.feed.height(Indexer.Height.POWER)
                }
            }

            lastLeftShift = leftShift
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

    infix fun Double.max(that: Double) = maxOf(this, that)
    fun Gamepad.shift() = right_trigger > .5 || left_trigger > .5
}