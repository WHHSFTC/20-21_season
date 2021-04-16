package org.firstinspires.ftc.teamcode.switchboard.hardware

import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.teamcode.switchboard.core.Frame
import org.firstinspires.ftc.teamcode.switchboard.core.Logger
import org.firstinspires.ftc.teamcode.switchboard.stores.*

class EncoderImpl(frame: Observable<Frame>, val m: DcMotorEx, val name: String, val logger: Logger): Encoder {
    var lastPosition = 0

    override val position = StartPoint(0).tap { log(logger.err, "$name pos") }
    override val velocity = StartPoint(0.0).tap { log(logger.err, "$name velo") }

    init {
        frame.subscribe {
            input(it)
        }
    }

    fun input(frame: Frame) {
        val pos = m.currentPosition
        var rawVelocity = m.velocity

        val derivative = (pos - lastPosition)/frame.step.seconds.toDouble()

        while (derivative - rawVelocity > (1 shl 16) / 2.0)
            rawVelocity += 1 shl 16

        while (derivative - rawVelocity < (1 shl 16) / -2.0)
            rawVelocity -= 1 shl 16

        position.value = pos
        velocity.value = rawVelocity

        lastPosition = pos
    }
}