package org.firstinspires.ftc.teamcode.switchboard.hw

import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.teamcode.switchboard.core.Logger
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

class EncoderImpl(val m: DcMotorEx, val name: String, val log: Logger): Encoder {
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

        log.err["$name pos"] = position
        log.err["$name velocity"] = velocity
    }
}