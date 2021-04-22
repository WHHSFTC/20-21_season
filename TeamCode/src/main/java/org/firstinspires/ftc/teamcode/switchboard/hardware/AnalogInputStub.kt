package org.firstinspires.ftc.teamcode.switchboard.hw

import org.firstinspires.ftc.teamcode.switchboard.core.Logger

class AnalogInputStub(val name: String, val log: Logger): AnalogInput {
    override val voltage: Double = 0.0
    override val maxVoltage: Double = 1.0
    override fun input() {
        log.out["[STUB] $name voltage"] = voltage
    }
}