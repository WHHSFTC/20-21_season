package org.firstinspires.ftc.teamcode.switchboard.hardware

import org.firstinspires.ftc.teamcode.switchboard.core.Log

class ServoImpl(val s: com.qualcomm.robotcore.hardware.Servo, val name: String, val log: Log): Servo {
    override var position: Double = 0.0
    override fun output() {
        if (s.position != position)
            s.position = position
        log.debug["$name pos"] = position
    }
}