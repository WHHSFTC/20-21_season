package org.firstinspires.ftc.teamcode.switchboard.hardware

import org.firstinspires.ftc.teamcode.switchboard.core.Log

class AnalogInputImpl(val ai: com.qualcomm.robotcore.hardware.AnalogInput, val name: String, val log: Log): AnalogInput {
    override var voltage: Double = 0.0
        private set
    override val maxVoltage: Double get() = ai.maxVoltage
    override fun input() {
        voltage = ai.voltage
        log.debug["$name voltage"] = voltage
    }
}