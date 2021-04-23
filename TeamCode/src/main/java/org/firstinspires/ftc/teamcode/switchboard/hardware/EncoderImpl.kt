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

    override var position: Int = 0
        private set
    override var velocity: Double = 0.0
        private set

    override fun input() {
        position = m.currentPosition
        rawVelocity = m.velocity

        val t = Time.now()
        val step = (t - lastTime).seconds.let { if (it epsilonEquals 0.0) EPSILON else it }

        val derivative = (position - lastPosition)/step

        var v = rawVelocity

        while (derivative - v > (1 shl 16) / 2.0)
            v += 1 shl 16

        while (derivative - v < (1 shl 16) / -2.0)
            v -= 1 shl 16

        velocity = v

        lastPosition = position
        lastTime = t

        log.err["$name pos"] = position
        log.err["$name velocity"] = velocity
    }

    override fun stopAndReset() {
        val mode = m.mode
        m.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        m.mode = mode
    }
}