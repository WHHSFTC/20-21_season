package org.firstinspires.ftc.teamcode.module

import com.qualcomm.robotcore.hardware.DcMotor

class HeightController(val bot: Robot) {
    val motor = bot.hwmap.dcMotor["aim"].also { it.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER }

    enum class Height(val pos: Int) {
        POWER((145.6 * 6).toInt()), HIGH(0);
    }

    val power = object : Module<Double> {
        override var state: Double = 0.0
            set(value) {
                motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
                motor.power = value
                field = value
            }
    }
    val height = object : Module<HeightController.Height> {
        override var state: Height = Height.HIGH
            set(value) {
                motor.targetPosition = value.pos
                motor.mode = DcMotor.RunMode.RUN_TO_POSITION
                motor.power = 1.0
                field = value
            }
    }
}