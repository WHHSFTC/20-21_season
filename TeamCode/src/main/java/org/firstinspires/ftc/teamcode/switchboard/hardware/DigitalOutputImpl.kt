package org.firstinspires.ftc.teamcode.switchboard.hardware

import com.qualcomm.robotcore.hardware.DigitalChannel
import org.firstinspires.ftc.teamcode.switchboard.core.Log

class DigitalOutputImpl(val dc: DigitalChannel, val name: String, val log: Log): DigitalOutput {
    override var high: Boolean = false
    override fun output() {
        if (dc.mode != DigitalChannel.Mode.OUTPUT)
            dc.mode = DigitalChannel.Mode.OUTPUT
        if (dc.state != high)
            dc.state = high

        log.debug["$name high"] = high
    }
}