package org.firstinspires.ftc.teamcode.switchboard.hardware

import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.teamcode.switchboard.core.Frame
import org.firstinspires.ftc.teamcode.switchboard.core.Logger
import org.firstinspires.ftc.teamcode.switchboard.stores.*

class EncoderImpl(frame: Observable<Frame>, val m: DcMotorEx, val name: String, val logger: Logger): Encoder {
    override val position = frame.map { m.currentPosition }
            .tap { log(logger.err, "$name pos") }
    override val velocity = (frame zip (frame.map { m.velocity } zip position.pairwise())).map { structure -> correctVelocity(structure) }
            .tap { log(logger.err, "$name velo") }

    fun correctVelocity(structure: Pair<Frame, Pair<Double, Pair<Int, Int>>>): Double {
        val (frame, s1) = structure
        val (raw, s2) = s1
        val (new, old) = s2

        var t = frame.step.seconds
        if (t == 0.0)
            t = 1.0

        val derivative = (new - old)/t

        var velo = raw

        while (derivative - velo > (1 shl 16) / 2.0)
            velo += 1 shl 16

        while (derivative - velo < (1 shl 16) / -2.0)
            velo -= 1 shl 16

        return velo
    }
}