package org.firstinspires.ftc.teamcode.switchboard.hw

import org.firstinspires.ftc.teamcode.switchboard.core.Logger

class DigitalOutputStub(val name: String, val log: Logger): DigitalOutput {
    override var high: Boolean = false
    override fun output(all: Boolean) {
        log.out["[STUB] $name high"] = high
    }
}