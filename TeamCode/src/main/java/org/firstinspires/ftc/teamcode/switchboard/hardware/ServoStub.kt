package org.firstinspires.ftc.teamcode.switchboard.hardware

import org.firstinspires.ftc.teamcode.switchboard.core.Logger

class ServoStub(val name: String, val logger: Logger): Servo {
    override var position: Double = 0.0
    override fun output(all: Boolean) {
        logger.out["[STUB] $name pos"] = position
    }
}