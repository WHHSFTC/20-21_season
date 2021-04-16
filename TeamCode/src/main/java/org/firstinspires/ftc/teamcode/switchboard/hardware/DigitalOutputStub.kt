package org.firstinspires.ftc.teamcode.switchboard.hardware

import org.firstinspires.ftc.teamcode.switchboard.core.Logger

class DigitalOutputStub(val name: String, val logger: Logger): DigitalOutput {
    override var high: Boolean = false
    override fun output(all: Boolean) {
        logger.out["[STUB] $name high"] = high
    }
}