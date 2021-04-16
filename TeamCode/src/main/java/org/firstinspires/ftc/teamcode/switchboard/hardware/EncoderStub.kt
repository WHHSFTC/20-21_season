package org.firstinspires.ftc.teamcode.switchboard.hardware

import org.firstinspires.ftc.teamcode.switchboard.core.Frame
import org.firstinspires.ftc.teamcode.switchboard.core.Logger
import org.firstinspires.ftc.teamcode.switchboard.stores.Observable
import org.firstinspires.ftc.teamcode.switchboard.stores.*

class EncoderStub(val frame: Observable<Frame>, val name: String, val logger: Logger): Encoder {
    override val position = frame.map { (Math.random() * 10.0).toInt() }.tap { log(logger.out, "[STUB] $name pos") }
    override val velocity = frame.map { Math.random() }.tap { log(logger.out, "[STUB] $name velo") }
}