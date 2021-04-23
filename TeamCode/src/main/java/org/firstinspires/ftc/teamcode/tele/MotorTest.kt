package org.firstinspires.ftc.teamcode.tele

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.module.CustomMecanumDrive
import org.firstinspires.ftc.teamcode.module.OpMode
import org.firstinspires.ftc.teamcode.switchboard.core.Activity
import org.firstinspires.ftc.teamcode.switchboard.core.Frame

@TeleOp
class MotorTest: OpMode(Mode.TELE) {
    override fun startHook() {
        bot.prependActivity(Controller())
    }

    inner class Controller: Activity {
        override fun load() {

        }

        override fun update(frame: Frame) {
            bot.dt.powers = CustomMecanumDrive.Powers(
                -gamepad1.left_stick_y.toDouble(),
                -gamepad1.right_stick_y.toDouble(),
                -gamepad2.left_stick_y.toDouble(),
                -gamepad2.right_stick_y.toDouble()
            )
        }
    }

    infix fun Double.max(that: Double) = maxOf(this, that)
}