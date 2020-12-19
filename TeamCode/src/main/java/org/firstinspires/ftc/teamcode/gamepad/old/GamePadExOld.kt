package org.firstinspires.ftc.teamcode.gamepad.old

import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.gamepad.*
import org.firstinspires.ftc.teamcode.gamepad.DPad
import org.firstinspires.ftc.teamcode.gamepad.Keys

class GamePadExOld(private val gamepad: Gamepad): GamePad {
    override val left: JoyStick = object : JoyStick {
        override val x: Double
            get() = gamepad.left_stick_x.toDouble()
        override val y: Double
            get() = gamepad.left_stick_y.toDouble()
    }

    override val right: JoyStick = object : JoyStick {
        override val x: Double
            get() = gamepad.right_stick_x.toDouble()
        override val y: Double
            get() = gamepad.right_stick_y.toDouble()
    }

    override val keys: Keys
        get() = GamePadKeysOld(gamepad)

    override val dpad: DPad = object : DPad {
        override val left: Boolean
            get() = gamepad.dpad_left
        override val right: Boolean
            get() = gamepad.dpad_right
        override val up: Boolean
            get() = gamepad.dpad_up
        override val down: Boolean
            get() = gamepad.dpad_down
    }

    override val guide: Boolean
        get() = gamepad.guide

    override val start: Boolean
        get() = gamepad.start
    override val back: Boolean
        get() = gamepad.back

    override val left_stick_button: Boolean
        get() = gamepad.left_stick_button
    override val right_stick_button: Boolean
        get() = gamepad.right_stick_button

    override val left_bumper: Boolean
        get() = gamepad.left_bumper
    override val right_bumper: Boolean
        get() = gamepad.right_bumper

    override val left_trigger: Double
        get() = gamepad.left_trigger.toDouble()
    override val right_trigger: Double
        get() = gamepad.right_trigger.toDouble()
}