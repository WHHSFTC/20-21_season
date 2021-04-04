package org.firstinspires.ftc.teamcode.switchboard.hardware

import com.qualcomm.robotcore.hardware.DigitalChannel
import org.firstinspires.ftc.teamcode.switchboard.core.Log

class DigitalInputStub(val name: String, val log: Log): DigitalInput {
    override val high: Boolean = false
    override fun input() {
        log["[STUB] $name high"] = high
    }
}