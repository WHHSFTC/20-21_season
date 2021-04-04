package org.firstinspires.ftc.teamcode.switchboard.hardware

import org.firstinspires.ftc.teamcode.switchboard.core.Log

class AnalogInputStub(val name: String, val log: Log): AnalogInput {
    override val voltage: Double = 0.0
    override val maxVoltage: Double = 1.0
    override fun input() {
        log["[STUB] $name voltage"] = voltage
    }
}