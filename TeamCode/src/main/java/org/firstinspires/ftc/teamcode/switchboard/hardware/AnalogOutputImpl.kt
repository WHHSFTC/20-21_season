package org.firstinspires.ftc.teamcode.switchboard.hardware

import org.firstinspires.ftc.teamcode.switchboard.core.Logger

class AnalogOutputImpl(val ao: com.qualcomm.robotcore.hardware.AnalogOutput, val name: String, val log: Logger): AnalogOutput {
    private var m: AnalogOutput.OutputMode? = null
    private var a: Int? = null
    private var f: Int? = null
    override var mode: AnalogOutput.OutputMode = AnalogOutput.OutputMode.VOLTAGE_PM4
    override var amplitude: Int = 0
    override var frequency: Int = 0
    override fun output(all: Boolean) {
        if (m == null || m != mode) {
            ao.setAnalogOutputMode(mode.code)
            m = mode
        }
        if (a == null || a != amplitude) {
            ao.setAnalogOutputVoltage(amplitude)
            a = amplitude
        }
        if (f == null || f != frequency) {
            ao.setAnalogOutputFrequency(frequency)
            f = frequency
        }

        log.err["$name mode"] = mode
        log.err["$name amplitude"] = amplitude
        log.err["$name frequency"] = frequency
    }
}