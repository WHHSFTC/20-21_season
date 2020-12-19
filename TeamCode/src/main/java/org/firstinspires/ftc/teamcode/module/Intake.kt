package org.firstinspires.ftc.teamcode.module

import com.qualcomm.robotcore.hardware.DcMotor

const val coef = 1.0

class Intake(bot: Robot): Module<Intake.Power> {
    val motor: DcMotor = bot.hwmap.dcMotor["intake"]
    override var state: Power = Power.OFF
        set(value) {
            motor.power = value.pow
            field = value
        }
    enum class Power(val pow: Double) {
        OUT(coef), IN(-coef), OFF(.0)
    }
}