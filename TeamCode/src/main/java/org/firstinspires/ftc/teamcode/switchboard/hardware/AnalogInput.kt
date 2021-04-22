package org.firstinspires.ftc.teamcode.switchboard.hw

interface AnalogInput: HardwareInput {
    val voltage: Double
    val maxVoltage: Double
}