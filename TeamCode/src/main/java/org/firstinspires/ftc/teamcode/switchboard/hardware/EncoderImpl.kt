package org.firstinspires.ftc.teamcode.switchboard.hardware

import com.acmerobotics.roadrunner.util.EPSILON
import com.acmerobotics.roadrunner.util.epsilonEquals
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.teamcode.switchboard.core.Logger
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

class EncoderImpl(val m: DcMotorEx, val name: String, val log: Logger): Encoder {
    var lastTime = Time.zero
    var lastPosition = 0

    var rawVelocity: Double = 0.0

    var rawPosition: Int = 0
        private set
    var offset: Int = 0
        private set

    override var position: Int
        get() = rawPosition + offset
        set(value) {
            offset = value - rawPosition
        }

    override var velocity: Double = 0.0
        private set

    override fun input() {
        rawPosition = m.currentPosition
        rawVelocity = m.velocity

        val t = Time.now()
        val step = (t - lastTime).seconds.let { if (it epsilonEquals 0.0) EPSILON else it }

        val derivative = (rawPosition - lastPosition)/step

        var v = rawVelocity

        while (derivative - v > (1 shl 16) / 2.0)
            v += 1 shl 16

        while (derivative - v < (1 shl 16) / -2.0)
            v -= 1 shl 16

        velocity = v

        lastPosition = rawPosition
        lastTime = t

        log.err["$name pos"] = rawPosition
        log.err["$name velocity"] = velocity
    }

    override fun stopAndReset() {
        val mode = m.mode
        m.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        m.mode = mode
        offset = 0
    }
}