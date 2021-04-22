package org.firstinspires.ftc.teamcode.switchboard.hw

import com.qualcomm.robotcore.hardware.DigitalChannel
import org.firstinspires.ftc.teamcode.switchboard.core.Logger

class DigitalInputImpl(val dc: DigitalChannel, val name: String, val log: Logger): DigitalInput {
    override var high: Boolean = false
        private set
    override fun input() {
        if (dc.mode != DigitalChannel.Mode.INPUT)
            dc.mode = DigitalChannel.Mode.INPUT
        high = dc.state
        log.err["$name high"] = high
    }
}