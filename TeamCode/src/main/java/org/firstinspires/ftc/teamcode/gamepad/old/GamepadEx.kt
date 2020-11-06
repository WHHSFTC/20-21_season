package org.firstinspires.ftc.teamcode.gamepad.old

class GamepadEx(val g: com.qualcomm.robotcore.hardware.Gamepad): Gamepad {
    override val left: Joystick = object : Joystick {
        override val x: Float
            get() = g.left_stick_x
        override val y: Float
            get() = g.left_stick_y
    }

    override val right: Joystick = object : Joystick {
        override val x: Float
            get() = g.right_stick_x
        override val y: Float
            get() = g.right_stick_y
    }

    override val dpad: DPad = object : DPad {
        override val left: Boolean
            get() = g.dpad_left
        override val right: Boolean
            get() = g.dpad_right
        override val up: Boolean
            get() = g.dpad_up
        override val down: Boolean
            get() = g.dpad_down
    }

    override val keys: Keys =
            if (g.type() == com.qualcomm.robotcore.hardware.Gamepad.Type.SONY_PS4)
                PSKeys(g)
            else
                XBoxKeys(g)

    override val guide: Boolean
        get() = g.guide

    override val start: Boolean
        get() = g.start
    override val back: Boolean
        get() = g.back

    override val left_stick_button: Boolean
        get() = g.left_stick_button
    override val right_stick_button: Boolean
        get() = g.right_stick_button

    override val left_bumper: Boolean
        get() = g.left_bumper
    override val right_bumper: Boolean
        get() = g.right_bumper

    override val left_trigger: Float
        get() = g.left_trigger
    override val right_trigger: Float
        get() = g.right_trigger

    // ps:
    override val share: Boolean
        get() = g.share
    override val options: Boolean
        get() = g.options
    override val touchpad: Boolean
        get() = g.touchpad
    override val ps: Boolean
        get() = g.ps
}