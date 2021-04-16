package org.firstinspires.ftc.teamcode.switchboard.hardware

import org.firstinspires.ftc.teamcode.switchboard.core.Frame
import org.firstinspires.ftc.teamcode.switchboard.core.Logger
import org.firstinspires.ftc.teamcode.switchboard.stores.*

class DigitalInputStub(frame: Observable<Frame>, val name: String, val logger: Logger): DigitalInput {
    override val high = frame.map { false }.tap { log(logger.out, "[STUB] $name high") }
}