package org.firstinspires.ftc.teamcode.switchboard.hardware

class AnalogInputImpl(val ai: com.qualcomm.robotcore.hardware.AnalogInput): AnalogInput {
    override var voltage: Double = 0.0
        private set
    override val maxVoltage: Double get() = ai.maxVoltage
    override fun input() {
        voltage = ai.voltage
    }
}