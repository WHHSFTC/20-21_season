package org.firstinspires.ftc.teamcode.switchboard.hw

import org.firstinspires.ftc.teamcode.switchboard.core.Logger

class MotorStub(val name: String, val log: Logger): Motor {
    override var power: Double = 0.0
    override var zpb: Motor.ZeroPowerBehavior = Motor.ZeroPowerBehavior.BRAKE

    override fun output(all: Boolean) {
        log.out["[STUB] $name power"] = power
        log.out["[STUB] $name zpb"] = zpb
    }
}