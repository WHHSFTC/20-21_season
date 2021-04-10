package org.firstinspires.ftc.teamcode.switchboard.hardware

import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.teamcode.switchboard.core.Log
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

class EncoderImpl(val m: DcMotorEx, val name: String, val log: Log): Encoder {
    var lastTime = Time.zero
    var lastPosition = 0

    var rawVelocity: Double = 0.0

    override var position: Int = 0
        private set
    override var velocity: Double = 0.0
        private set

    override fun input() {
        position = m.currentPosition
        rawVelocity = m.velocity

        val t = Time.now()
        val derivative = (position - lastPosition)/(t - lastTime).seconds.toDouble()

        lastPosition = position
        lastTime = t

        log.debug["$name pos"] = position
        log.debug["$name velocity"] = velocity
    }
}