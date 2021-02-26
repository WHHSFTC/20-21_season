package org.firstinspires.ftc.teamcode.module

import kotlinx.coroutines.delay
import org.firstinspires.ftc.teamcode.test.QuickDrop

class Wobble(val bot: Robot) {
    enum class ElbowState(override val pos: Double): StatefulServo.ServoPosition {
        STORE(1.0), DROP(.47), CARRY(.9), INTAKE(.32), RING(.16)
    }

    enum class ClawState(override val pos: Double): StatefulServo.ServoPosition {
        OPEN(.15), CLOSED(.65)
    }

    var elbow = StatefulServo<ElbowState>(bot.hwmap.servo["elbow"], ElbowState.STORE)

    var claw = StatefulServo<ClawState>(bot.hwmap.servo["claw"], ClawState.CLOSED)

    suspend fun quickDrop() {
        elbow(Wobble.ElbowState.DROP)
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