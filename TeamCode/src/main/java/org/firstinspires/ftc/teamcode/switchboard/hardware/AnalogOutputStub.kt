package org.firstinspires.ftc.teamcode.switchboard.hardware

import org.firstinspires.ftc.teamcode.switchboard.core.Logger

class AnalogOutputStub(val name: String, val logger: Logger): AnalogOutput {
    private var m: AnalogOutput.OutputMode? = null
    override var mode: AnalogOutput.OutputMode = AnalogOutput.OutputMode.VOLTAGE_PM4
    override var amplitude: Int = 0
    override var frequency: Int = 0
    override fun output(all: Boolean) {
        logger.out["[STUB] $name mode"] = mode
        logger.out["[STUB] $name amplitude"] = amplitude
        logger.out["[STUB] $name frequency"] = frequency
    }
}