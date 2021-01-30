package org.firstinspires.ftc.teamcode.module

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.DcMotor

@Config
class Intake(val bot: Robot): Module<Intake.Power> {
    companion object {
        @JvmField var INTAKE_COEF: Double = .8
    }
    val motor: DcMotor = bot.hwmap.dcMotor["intake"]
    //val motor: DcMotor = bot.hwmap.dcMotor["intake"].also { it.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER; it.mode = DcMotor.RunMode.RUN_USING_ENCODER }
    override var state: Power = Power.OFF
        set(value) {
            motor.power = value.pow * INTAKE_COEF * 12.0 / bot.dt.batteryVoltageSensor.voltage
            //if (value != field)
                //motor.power = value.pow * INTAKE_COEF
            field = value
        }
    enum class Power(val pow: Double) {
        OUT(1.0), IN(-1.0), OFF(.0)
    }
}