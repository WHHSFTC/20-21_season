package org.firstinspires.ftc.teamcode.module

class Wobble(val bot: Robot) {
    enum class ElbowState(override val pos: Double): StatefulServo.ServoPosition {
        STORE(.02), DROP(.6), CARRY(.15), INTAKE(.9)
    }

    enum class ClawState(override val pos: Double): StatefulServo.ServoPosition {
        OPEN(.29), CLOSED(.65)
    }

    var elbow = StatefulServo<ElbowState>(bot.hwmap.servo["elbow"], ElbowState.STORE)

    var claw = StatefulServo<ClawState>(bot.hwmap.servo["claw"], ClawState.CLOSED)
}