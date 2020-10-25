package org.firstinspires.ftc.teamcode.module

class Wobble(val bot: Robot) {
    enum class ElbowState(override val pos: Double): StatefulServo.ServoPosition {
        UP(.2), OUT(.69), INIT(1.0)
    }

    enum class ClawState(override val pos: Double): StatefulServo.ServoPosition {
        OPEN(.72), CLOSED(.22)
    }

    var elbow = StatefulServo<ElbowState>(bot.hwmap.servo["elbow"], ElbowState.INIT)

    var claw = StatefulServo<ClawState>(bot.hwmap.servo["claw"], ClawState.CLOSED)
}