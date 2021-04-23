package org.firstinspires.ftc.teamcode.switchboard.hardware

import org.firstinspires.ftc.teamcode.switchboard.core.Logger

class ServoImpl(val s: com.qualcomm.robotcore.hardware.Servo, val name: String, val log: Logger): Servo {
    override var position: Double = 0.0
    override fun output(all: Boolean) {
        if (s.position != position)
            s.position = position
        log.err["$name pos"] = position
    }
}