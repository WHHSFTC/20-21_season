package org.firstinspires.ftc.teamcode.switchboard.hardware

import com.qualcomm.robotcore.hardware.DigitalChannel
import org.firstinspires.ftc.teamcode.switchboard.core.Frame
import org.firstinspires.ftc.teamcode.switchboard.core.Logger
import org.firstinspires.ftc.teamcode.switchboard.stores.*

class DigitalInputImpl(val frame: Observable<Frame>, val dc: DigitalChannel, val name: String, val logger: Logger): DigitalInput {
    override var high = frame.map { dc.state }.tap { log(logger.err, "$name high") }

    init {
        if (dc.mode != DigitalChannel.Mode.INPUT)
            dc.mode = DigitalChannel.Mode.INPUT
    }
}