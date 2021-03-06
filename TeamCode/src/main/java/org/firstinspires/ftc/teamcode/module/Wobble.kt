package org.firstinspires.ftc.teamcode.module

import kotlinx.coroutines.delay
import org.firstinspires.ftc.teamcode.test.QuickDrop

class Wobble(val bot: Robot) {
    enum class ElbowState(override val pos: Double): StatefulServo.ServoPosition {
        STORE(.05), DROP(.8), CARRY(.32), INTAKE(.95), WALL(.65)
    }

    enum class ClawState(override val pos: Double): StatefulServo.ServoPosition {
        OPEN(.55), CLOSED(1.0), WIDE(.27)
    }

    var elbow = StatefulServo<ElbowState>(bot.hwmap.servo["elbow"], ElbowState.STORE)

    var claw = StatefulServo<ClawState>(bot.hwmap.servo["claw"], ClawState.CLOSED)

    suspend fun quickDrop() {
        elbow(Wobble.ElbowState.WALL)
        delay(a.toLong())
        claw(Wobble.ClawState.OPEN)
        delay(b.toLong())
        elbow(Wobble.ElbowState.CARRY)
    }

    companion object {
        @JvmField var a = 300
        @JvmField var b = 100
    }
}