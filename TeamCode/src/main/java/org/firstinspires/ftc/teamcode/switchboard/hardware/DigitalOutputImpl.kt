package org.firstinspires.ftc.teamcode.switchboard.hardware

import com.qualcomm.robotcore.hardware.DigitalChannel

class DigitalOutputImpl(val dc: DigitalChannel): DigitalOutput {
    override var high: Boolean = false
    override fun output() {
        if (dc.mode != DigitalChannel.Mode.OUTPUT)
            dc.mode = DigitalChannel.Mode.OUTPUT
        if (dc.state != high)
            dc.state = high
    }
}