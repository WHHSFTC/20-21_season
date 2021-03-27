package org.firstinspires.ftc.teamcode.switchboard.hardware

class ServoImpl(val s: com.qualcomm.robotcore.hardware.Servo): Servo {
    override var position: Double = 0.0
    override fun output() {
        if (s.position != position)
            s.position = position
    }
}