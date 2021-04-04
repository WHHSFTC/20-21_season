package org.firstinspires.ftc.teamcode.switchboard.hardware

import org.firstinspires.ftc.teamcode.switchboard.core.Log

class ServoStub(val name: String, val log: Log): Servo {
    override var position: Double = 0.0
    override fun output() {
        log["[STUB] $name pos"] = position
    }
}