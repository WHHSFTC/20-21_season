package org.firstinspires.ftc.teamcode.switchboard.hardware

import org.firstinspires.ftc.teamcode.switchboard.core.Log

class AnalogOutputStub(val name: String, val log: Log): AnalogOutput {
    private var m: AnalogOutput.OutputMode? = null
    override var mode: AnalogOutput.OutputMode = AnalogOutput.OutputMode.VOLTAGE_PM4
    override var amplitude: Int = 0
    override var frequency: Int = 0
    override fun output() {
        log["[STUB] $name mode"] = mode
        log["[STUB] $name amplitude"] = amplitude
        log["[STUB] $name frequency"] = frequency
    }
}