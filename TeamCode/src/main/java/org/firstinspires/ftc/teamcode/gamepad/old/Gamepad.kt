package org.firstinspires.ftc.teamcode.gamepad.old

interface Joystick {
    val x: Float
    val y: Float
}

interface DPad {
    val left: Boolean
    val right: Boolean
    val up: Boolean
    val down: Boolean
}

interface Keys {
    val a: Boolean
    val b: Boolean
    val x: Boolean
    val y: Boolean

    // ps
    //val circle: Boolean
    //val cross: Boolean
    //val triangle: Boolean
    //val square: Boolean
}

interface Gamepad {
    val left: Joystick
    val right: Joystick
    val keys: Keys
    val dpad: DPad

    val guide: Boolean
    val start: Boolean
    val back: Boolean

    val left_stick_button: Boolean
    val right_stick_button: Boolean

    val left_bumper: Boolean
    val right_bumper: Boolean

    val left_trigger: Float
    val right_trigger: Float

    // ps:
    val share: Boolean
    val options: Boolean
    val touchpad: Boolean
    val ps: Boolean
}
