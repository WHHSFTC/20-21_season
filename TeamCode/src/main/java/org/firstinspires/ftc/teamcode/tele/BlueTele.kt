package org.firstinspires.ftc.teamcode.tele

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.module.HeightController
import org.firstinspires.ftc.teamcode.module.OpMode
import org.firstinspires.ftc.teamcode.module.Shooter
import org.firstinspires.ftc.teamcode.switchboard.core.Activity
import org.firstinspires.ftc.teamcode.switchboard.core.Frame
import kotlin.math.absoluteValue
import kotlin.math.pow

@TeleOp
class BlueTele: OpMode(Mode.TELE) {
    override fun startHook() {
        bot.prependActivity(Andrew())
        bot.prependActivity(Adham())
    }

    inner class Andrew: Activity {
        override fun update(frame: Frame) {
            val turtle = gamepad1.left_trigger > .5 || gamepad1.right_trigger > .5
            val x = (-gamepad1.left_stick_y).toDouble()
            val y = (-gamepad1.left_stick_x).toDouble()
            val omega = (-gamepad1.right_stick_x).toDouble()

            val linearScalar = (x.absoluteValue max y.absoluteValue).pow(2.0)
            val turtleScalar = if (turtle) 3.0 else 1.0

            val twist = Pose2d(
                    x = linearScalar * x,
                    y = linearScalar * y,
                    omega
            ) / turtleScalar

            bot.dt.botTwist = twist
        }
    }

    inner class Adham: Activity {
        var lastA = false
        var lastB = false
        var lastDeadzone = true

        override fun update(frame: Frame) {
            val a = gamepad2.a
            val b = gamepad2.b
            val shift = gamepad2.shift()

            when {
                a and !lastA -> {
                    bot.out(bot.aim.height().power)
                }

                b and !lastB -> {
                    bot.out(Shooter.State.OFF)
                }
            }

            val p = -gamepad2.right_stick_y
            val deadzone = p.absoluteValue < 0.1

            if (shift) {
                if (!deadzone)
                    bot.aim.power(p.toDouble() / 4.0)
                else if (!lastDeadzone)
                    bot.aim.power(0.0)
                else
                    when {
                        gamepad2.dpad_down -> bot.aim.reset()
                        gamepad2.dpad_up -> bot.aim.height(HeightController.Height.HIGH)
                        gamepad2.dpad_left || gamepad2.dpad_right -> bot.aim.height(HeightController.Height.POWER)
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