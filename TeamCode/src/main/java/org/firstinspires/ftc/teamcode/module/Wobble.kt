package org.firstinspires.ftc.teamcode.module

class Wobble(val bot: Robot) {
    enum class ElbowState(override val pos: Double): StatefulServo.ServoPosition {
        STORE(.82), DROP(.26), CARRY(.64), INTAKE(.12)
    }

    enum class ClawState(override val pos: Double): StatefulServo.ServoPosition {
        OPEN(.95), CLOSED(.4)
    }

    var elbow = StatefulServo<ElbowState>(bot.hwmap.servo["elbow"], ElbowState.STORE)

    var claw = StatefulServo<ClawState>(bot.hwmap.servo["claw"], ClawState.CLOSED)
}