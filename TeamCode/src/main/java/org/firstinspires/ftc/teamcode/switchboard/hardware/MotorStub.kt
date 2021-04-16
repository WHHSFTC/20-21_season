package org.firstinspires.ftc.teamcode.switchboard.hardware

import org.firstinspires.ftc.teamcode.switchboard.core.Logger

class MotorStub(val name: String, val logger: Logger): Motor {
    override var power: Double = 0.0
    override var zpb: Motor.ZeroPowerBehavior = Motor.ZeroPowerBehavior.BRAKE

    override fun output(all: Boolean) {
        logger.out["[STUB] $name power"] = power
        logger.out["[STUB] $name zpb"] = zpb
    }
}