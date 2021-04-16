package org.firstinspires.ftc.teamcode.switchboard.hardware

import org.firstinspires.ftc.teamcode.switchboard.core.Frame
import org.firstinspires.ftc.teamcode.switchboard.core.Logger
import org.firstinspires.ftc.teamcode.switchboard.stores.Observable
import org.firstinspires.ftc.teamcode.switchboard.stores.log
import org.firstinspires.ftc.teamcode.switchboard.stores.map
import org.firstinspires.ftc.teamcode.switchboard.stores.tap

class AnalogInputImpl(val frame: Observable<Frame>, val ai: com.qualcomm.robotcore.hardware.AnalogInput, val name: String, val logger: Logger): AnalogInput {
    override val voltage = frame.map { ai.voltage }.tap { log(logger.err, "$name voltage") }
    override val maxVoltage = frame.map { ai.maxVoltage }
}