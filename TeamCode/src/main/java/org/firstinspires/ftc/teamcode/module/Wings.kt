package org.firstinspires.ftc.teamcode.module

import org.firstinspires.ftc.teamcode.switchboard.core.*

class Wings(config: Configuration, val logger: Logger) : Module<Wings.State> {
    enum class State(val left: Double, val right: Double) {
        UP(.01, .97), DOWN(0.41, .57)
    }

    val leftWing = config.servos["leftWing"]
    val rightWing = config.servos["rightWing"]

    override var state: State = State.UP
        set(value) {
            leftWing.position = value.left
            rightWing.position = value.right
            field = value
        }

    init {
        this(State.UP)
    }
}