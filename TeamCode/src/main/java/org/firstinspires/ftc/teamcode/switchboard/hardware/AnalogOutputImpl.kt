package org.firstinspires.ftc.teamcode.switchboard.hardware

class AnalogOutputImpl(val ao: com.qualcomm.robotcore.hardware.AnalogOutput): AnalogOutput {
    private var m: AnalogOutput.OutputMode? = null
    private var miv: Int? = null
    private var f: Int? = null
    override var mode: AnalogOutput.OutputMode = AnalogOutput.OutputMode.VOLTAGE_PM4
    override var milbivolts: Int = 0
    override var frequency: Int = 0
    override fun output() {
        if (m == null || m != mode) {
            ao.setAnalogOutputMode(mode.code)
            m = mode
        }
        if (miv == null || miv != milbivolts) {
            ao.setAnalogOutputVoltage(milbivolts)
            miv = milbivolts
        }
        if (f == null || f != frequency) {
            ao.setAnalogOutputFrequency(frequency)
            f = frequency
        }
    }
}