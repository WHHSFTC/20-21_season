package org.firstinspires.ftc.teamcode.switchboard.hardware

interface AnalogOutput: HardwareOutput {
    enum class OutputMode(val code: Byte) {
        VOLTAGE_PM4(0), SINE_0to8(1), SQUARE_0to8(2), TRIANGLE_0to8(3)
    }
    var mode: OutputMode
    var amplitude: Int
    var frequency: Int
}