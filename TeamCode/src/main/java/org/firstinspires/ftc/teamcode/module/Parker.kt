package org.firstinspires.ftc.teamcode.module

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.DcMotor

@Config
class Parker(val bot: Robot): Module<Parker.Power> {
    val motor = VeloCR(bot.hwmap.crservo["tape"])

    override var state: Power = Power.OFF
        set(value) {
            motor(value.pow)
            field = value
        }

    enum class Power(val pow: Double) {
        OUT(-1.0), IN(1.0), OFF(.0)
    }
}