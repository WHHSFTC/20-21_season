package org.firstinspires.ftc.teamcode.switchboard.hardware

import org.firstinspires.ftc.teamcode.switchboard.core.Logger

class AnalogInputImpl(val ai: com.qualcomm.robotcore.hardware.AnalogInput, val name: String, val log: Logger): AnalogInput {
    override var voltage: Double = 0.0
        private set
    override val maxVoltage: Double get() = ai.maxVoltage
    override fun input() {
        voltage = ai.voltage
        log.err["$name voltage"] = voltage
    }
}