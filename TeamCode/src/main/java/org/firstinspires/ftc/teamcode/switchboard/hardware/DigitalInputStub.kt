package org.firstinspires.ftc.teamcode.switchboard.hardware

import org.firstinspires.ftc.teamcode.switchboard.core.Logger

class DigitalInputStub(val name: String, val log: Logger): DigitalInput {
    override val high: Boolean = false
    override fun input() {
        log.out["[STUB] $name high"] = high
    }
}