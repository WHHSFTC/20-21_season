package org.firstinspires.ftc.teamcode.switchboard.hardware

import com.acmerobotics.roadrunner.util.epsilonEquals
import org.firstinspires.ftc.teamcode.switchboard.core.Logger

class ServoImpl(val s: com.qualcomm.robotcore.hardware.Servo, val name: String, val log: Logger): Servo {
    //override var position: Double = 0.0
    override var position: Double = s.position
    var touched = false
        private set
    var n = 0
        private set
    override fun output(all: Boolean) {
        val g = s.position
        if (all || !touched || !(g epsilonEquals position)) {
            if (!touched) {
                s.position = position + if (position < .5) +0.01 else -0.01
            }

            s.position = position

            log.err["$name touched"] = touched
            n++
            touched = true

            log.err["$name pos"] = position
            log.err["$name get"] = g
            log.err["$name count"] = n
        }
    }
}