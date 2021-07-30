package org.firstinspires.ftc.teamcode.module

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.switchboard.core.Activity
import org.firstinspires.ftc.teamcode.switchboard.core.Configuration
import org.firstinspires.ftc.teamcode.switchboard.core.Frame
import org.firstinspires.ftc.teamcode.switchboard.core.Logger

class Wobble(config: Configuration, val logger: Logger) : Activity {
    enum class ElbowState(override val pos: Double): StatefulServo.ServoPosition {
        STORE(.1), DROP(.85), CARRY(.32), INTAKE(1.0), WALL(.65)
    }

    enum class ClawState(override val pos: Double): StatefulServo.ServoPosition {
        //OPEN(.6), CLOSED(.07), WIDE(.99)
        OPEN(.6), CLOSED(1.0), WIDE(.35)
    }

    var elbow = StatefulServo<ElbowState>(config.servos["elbow"], ElbowState.STORE)

    var claw = StatefulServo<ClawState>(config.servos["claw"], ClawState.CLOSED)

    sealed class WobbleState : Activity {
        class QuickDrop(val initial: Frame, val wob: Wobble) : WobbleState() {
            var step = 0
            override fun update(frame: Frame) {
                when (step) {
                    0 -> {
                        wob.elbow(ElbowState.WALL)
                        step++
                    }

                    1 -> {
                        if ((frame.time - initial.time).milliseconds > a)
                            step++
                    }

                    2 -> {
                        wob.claw(ClawState.WIDE)
                        step++
                    }

                    3 -> {
                        if ((frame.time - initial.time).milliseconds > a + b)
                            step++
                    }

                    4 -> {
                        wob.elbow(ElbowState.CARRY)
                        step++
                    }

                    else -> {
                        wob.state = idle
                    }
                }
            }
        }

        object idle : WobbleState() {
            override fun update(frame: Frame) { }
        }
    }

    private var state: WobbleState = WobbleState.idle

    fun quickDrop(frame: Frame) {
        state = WobbleState.QuickDrop(frame, this)
    }

    override fun load() {
        claw(ClawState.CLOSED)
        elbow(ElbowState.STORE)
    }

    override fun update(frame: Frame) {
        state.update(frame)
    }

    companion object {
        @JvmField var a = 300
        @JvmField var b = 100
    }
}