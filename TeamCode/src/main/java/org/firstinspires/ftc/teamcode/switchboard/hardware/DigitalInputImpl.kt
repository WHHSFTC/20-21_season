package org.firstinspires.ftc.teamcode.switchboard.hardware

import com.qualcomm.robotcore.hardware.DigitalChannel
import org.firstinspires.ftc.teamcode.switchboard.core.Log

class DigitalInputImpl(val dc: DigitalChannel, val name: String, val log: Log): DigitalInput {
    override var high: Boolean = false
        private set
    override fun input() {
        if (dc.mode != DigitalChannel.Mode.INPUT)
            dc.mode = DigitalChannel.Mode.INPUT
        high = dc.state
        log.debug["$name high"] = high
    }
}