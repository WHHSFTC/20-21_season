package org.firstinspires.ftc.teamcode.tele

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.module.HeightController
import org.firstinspires.ftc.teamcode.module.OpMode
import org.firstinspires.ftc.teamcode.module.Shooter
import org.firstinspires.ftc.teamcode.module.Wobble
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
            val turtle = pad.left_trigger > .5 || pad.right_trigger > .5
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

        override fun update(frame: Frame) {
            drivetrain(frame)
            wobble(frame)
        }
    }

    inner class Adham(val pad: Gamepad): Activity {
        var lastA = false
        var lastB = false
        var lastDeadzone = true

        override fun update(frame: Frame) {
            val a = pad.a
            val b = pad.b
            val shift = pad.shift()

            when {
                a and !lastA -> {
                    bot.out(bot.aim.height().power)
                }

                b and !lastB -> {
                    bot.out(Shooter.State.OFF)
                }
            }

            val p = -pad.right_stick_y
            val deadzone = p.absoluteValue < 0.1

            if (shift) {
                if (!deadzone)
                    bot.aim.power(p.toDouble() / 4.0)
                else if (!lastDeadzone)
                    bot.aim.power(0.0)
                else
                    when {
                        pad.dpad_down -> bot.aim.reset()
                        pad.dpad_up -> bot.aim.height(HeightController.Height.HIGH)
                        pad.dpad_left || pad.dpad_right -> bot.aim.height(HeightController.Height.POWER)
                    }

            }

            lastDeadzone = deadzone
            lastA = a
            lastB = b
        }
    }

    infix fun Double.max(that: Double) = maxOf(this, that)
    fun Gamepad.shift() = right_trigger > .5 || left_trigger > .5
}