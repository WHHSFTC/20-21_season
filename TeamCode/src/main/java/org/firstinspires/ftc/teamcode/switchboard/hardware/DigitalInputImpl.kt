package org.firstinspires.ftc.teamcode.switchboard.hardware

import com.qualcomm.robotcore.hardware.DigitalChannel

class DigitalInputImpl(val dc: DigitalChannel): DigitalInput {
    override var high: Boolean = false
        private set
    override fun input() {
        if (dc.mode != DigitalChannel.Mode.INPUT)
            dc.mode = DigitalChannel.Mode.INPUT
        high = dc.state
    }
}