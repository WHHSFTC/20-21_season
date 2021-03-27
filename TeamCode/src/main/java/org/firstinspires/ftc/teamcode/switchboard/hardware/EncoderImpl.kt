package org.firstinspires.ftc.teamcode.switchboard.hardware

import com.qualcomm.robotcore.hardware.DcMotorEx

class EncoderImpl(val m: DcMotorEx): Encoder {
    override var position: Int = 0
        private set
    override var velocity: Double = 0.0
        private set
    override fun input() {
        position = m.currentPosition
        velocity = m.velocity
    }
}