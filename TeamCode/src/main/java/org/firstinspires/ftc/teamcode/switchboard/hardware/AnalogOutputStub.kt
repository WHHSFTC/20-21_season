package org.firstinspires.ftc.teamcode.switchboard.hardware

import org.firstinspires.ftc.teamcode.switchboard.core.Logger

class AnalogOutputStub(val name: String, val log: Logger): AnalogOutput {
    private var m: AnalogOutput.OutputMode? = null
    override var mode: AnalogOutput.OutputMode = AnalogOutput.OutputMode.VOLTAGE_PM4
    override var amplitude: Int = 0
    override var frequency: Int = 0
    override fun output(all: Boolean) {
        log.out["[STUB] $name mode"] = mode
        log.out["[STUB] $name amplitude"] = amplitude
        log.out["[STUB] $name frequency"] = frequency
    }
}