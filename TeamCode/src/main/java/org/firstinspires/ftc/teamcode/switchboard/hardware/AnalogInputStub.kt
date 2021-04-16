package org.firstinspires.ftc.teamcode.switchboard.hardware

import org.firstinspires.ftc.teamcode.switchboard.core.Logger
import org.firstinspires.ftc.teamcode.switchboard.core.Frame
import org.firstinspires.ftc.teamcode.switchboard.stores.*

class AnalogInputStub(val frame: Observable<Frame>, val name: String, val logger: Logger): AnalogInput {
    override val voltage = frame.map { 0.0 }.tap { log(logger.out, "[STUB] $name voltage") }
    override val maxVoltage = frame.map { 0.0 }
}