package org.firstinspires.ftc.teamcode.switchboard.hardware

import org.firstinspires.ftc.teamcode.switchboard.core.Logger
import org.firstinspires.ftc.teamcode.switchboard.stores.Observer

class MotorStub(val name: String, val logger: Logger): Motor {
    override val power = Observer<Double> { logger.out["[STUB] $name power"] }
    override val zpb = Observer<Motor.ZeroPowerBehavior> { logger.out["[STUB] $name zpb"] }

    override fun output(all: Boolean) { }
}