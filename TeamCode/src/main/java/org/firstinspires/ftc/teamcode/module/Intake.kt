package org.firstinspires.ftc.teamcode.module

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.DcMotor

@Config
class Intake(bot: Robot): Module<Intake.Power> {
    companion object {
        @JvmField var INTAKE_COEF: Double = 1.0
    }
    val motor: DcMotor = bot.hwmap.dcMotor["intake"]
    override var state: Power = Power.OFF
        set(value) {
            motor.power = value.pow * INTAKE_COEF
            field = value
        }
    enum class Power(val pow: Double) {
        OUT(1.0), IN(-1.0), OFF(.0)
    }
}