package org.firstinspires.ftc.teamcode.switchboard.hardware

import org.firstinspires.ftc.teamcode.switchboard.core.Log

class DigitalOutputStub(val name: String, val log: Log): DigitalOutput {
    override var high: Boolean = false
    override fun output() {
        log["[STUB] $name high"] = high
    }
}