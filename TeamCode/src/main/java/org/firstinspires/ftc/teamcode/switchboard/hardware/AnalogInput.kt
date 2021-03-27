package org.firstinspires.ftc.teamcode.switchboard.hardware

interface AnalogInput: HardwareInput {
    val voltage: Double
    val maxVoltage: Double
}