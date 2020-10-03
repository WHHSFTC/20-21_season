package org.firstinspires.ftc.teamcode.module

import com.qualcomm.robotcore.hardware.DcMotor

class Intake(bot: Robot) {
    val motor: DcMotor = bot.hwmap.dcMotor["intake"]
    var power: Power = Power.OFF
        set(value) {
            motor.power = value.pow
            field = value
        }
    enum class Power(val pow: Double) {
        OUT(-1.0), IN(1.0), OFF(.0)
    }
}