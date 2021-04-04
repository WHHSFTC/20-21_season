package org.firstinspires.ftc.teamcode.switchboard.hardware

import org.firstinspires.ftc.teamcode.switchboard.core.Log

class EncoderStub(val name: String, val log: Log): Encoder {
    override val position: Int = 0
    override val velocity: Double = 0.0

    override fun input() {
        log["[STUB] $name pos"] = position
        log["[STUB] $name velocity"] = velocity
    }
}