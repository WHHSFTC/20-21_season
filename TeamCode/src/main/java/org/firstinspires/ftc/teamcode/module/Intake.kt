package org.firstinspires.ftc.teamcode.module

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.switchboard.core.Configuration

@Config
class Intake(val bot: Summum, config: Configuration): Module<Intake.Power> {
    companion object {
        @JvmField var INTAKE_COEF: Double = 1.0
    }
    val motor = config.motors["intake"]
    val hookServo = config.servos["hook"]

    enum class HookPosition(override val pos: Double): StatefulServo.ServoPosition {
        LOCKED(.23), UNLOCKED(.9)
    }

    val hook = StatefulServo<Intake.HookPosition>(hookServo, HookPosition.LOCKED)

    override var state: Power = Power.OFF
        set(value) {
            when (value) {
                Power.OUT -> {
                    motor.power = value.pow
                }
                else -> {
                    motor.power = value.pow * INTAKE_COEF * 12.0 / bot.dt.batteryVoltageSensor.voltage
                }
            }
            field = value
        }
    enum class Power(val pow: Double) {
        OUT(-1.0), IN(1.0), OFF(.0)
    }
}