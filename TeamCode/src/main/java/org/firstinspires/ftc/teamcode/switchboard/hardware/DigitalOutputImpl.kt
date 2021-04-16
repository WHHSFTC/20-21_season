package org.firstinspires.ftc.teamcode.switchboard.hardware

import com.qualcomm.robotcore.hardware.DigitalChannel
import org.firstinspires.ftc.teamcode.switchboard.core.Logger

class DigitalOutputImpl(val dc: DigitalChannel, val name: String, val logger: Logger): DigitalOutput {
    override var high: Boolean = false
    override fun output(all: Boolean) {
        if (dc.mode != DigitalChannel.Mode.OUTPUT)
            dc.mode = DigitalChannel.Mode.OUTPUT
        if (dc.state != high)
            dc.state = high

        logger.err["$name high"] = high
    }
}