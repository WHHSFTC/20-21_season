package org.firstinspires.ftc.teamcode.switchboard.hardware

import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.teamcode.switchboard.core.Log

class EncoderImpl(val m: DcMotorEx, val name: String, val log: Log): Encoder {
    override var position: Int = 0
        private set
    override var velocity: Double = 0.0
        private set
    override fun input() {
        position = m.currentPosition
        velocity = m.velocity

        log.debug["$name pos"] = position
        log.debug["$name velocity"] = velocity
    }
}