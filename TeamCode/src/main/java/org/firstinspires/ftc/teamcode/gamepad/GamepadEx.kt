package org.firstinspires.ftc.teamcode.gamepad

import com.qualcomm.robotcore.hardware.Gamepad

class GamepadEx(val gamepad: Gamepad) {
    private val buttons = GamepadKeys.Button.values()

    private var buttonReaders: Map<GamepadKeys.Button, ButtonReader> =
            emptyMap<GamepadKeys.Button, ButtonReader>().toMutableMap()

    init {
        for(button in buttons) {
            (buttonReaders as MutableMap<GamepadKeys.Button, ButtonReader>)[button] = ButtonReader(this, button)
        }
    }

    operator fun get(button: GamepadKeys.Button): Boolean = when (button) {
            GamepadKeys.Button.A -> gamepad.a
            GamepadKeys.Button.B -> gamepad.b
            GamepadKeys.Button.X -> gamepad.x
            GamepadKeys.Button.Y -> gamepad.y
            GamepadKeys.Button.LEFT_BUMPER -> gamepad.left_bumper
            GamepadKeys.Button.RIGHT_BUMPER -> gamepad.right_bumper
            GamepadKeys.Button.DPAD_UP -> gamepad.dpad_up
            GamepadKeys.Button.DPAD_DOWN -> gamepad.dpad_down
            GamepadKeys.Button.DPAD_LEFT -> gamepad.dpad_left
            GamepadKeys.Button.DPAD_RIGHT -> gamepad.dpad_right
            GamepadKeys.Button.BACK -> gamepad.back
            GamepadKeys.Button.START -> gamepad.start
            GamepadKeys.Button.LEFT_STICK_BUTTON -> gamepad.left_stick_button
            GamepadKeys.Button.RIGHT_STICK_BUTTON -> gamepad.right_stick_button
    }

    operator fun get(trigger: GamepadKeys.Trigger): Double = when(trigger) {
        GamepadKeys.Trigger.LEFT_TRIGGER -> gamepad.left_trigger.toDouble()
        GamepadKeys.Trigger.RIGHT_TRIGGER -> gamepad.right_trigger.toDouble()
    }

    fun getLeftStickY(invert: Boolean = true) = (if(invert) -1 else 1) * gamepad.left_stick_y

    fun getRightStickY(invert: Boolean = true) = (if(invert) -1 else 1) * gamepad.right_stick_y

    fun getLeftStickX(invert: Boolean = false) = (if(invert) -1 else 1) * gamepad.left_stick_x

    fun getRightStickX(invert: Boolean = false) = (if(invert) -1 else 1) * gamepad.right_stick_x

    fun wasJustPressed(button: GamepadKeys.Button) = buttonReaders.getValue(button).wasJustPressed()

    fun wasJustReleased(button: GamepadKeys.Button) = buttonReaders.getValue(button).wasJustReleased()

    fun readButtons() = buttons.forEach {
        buttonReaders.getValue(it).readValue()
    }

    fun isDown(button: GamepadKeys.Button) = buttonReaders.getValue(button).isDown()

    fun stateJustChanged(button: GamepadKeys.Button) = buttonReaders.getValue(button).stateJustChanged()
}