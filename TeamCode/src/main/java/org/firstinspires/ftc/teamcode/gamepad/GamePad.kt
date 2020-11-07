package org.firstinspires.ftc.teamcode.gamepad

interface GamePad {
    val left: JoyStick
    val right: JoyStick

    val keys: Keys

    val dpad: DPad

    val guide: Boolean
    val start: Boolean
    val back: Boolean

    val left_stick_button: Boolean
    val right_stick_button: Boolean

    val left_bumper: Boolean
    val right_bumper: Boolean

    val left_trigger: Double
    val right_trigger: Double
}