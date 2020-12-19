package org.firstinspires.ftc.teamcode.gamepad

class ToggleButtonReader(
        private var currentToggleState: Boolean = false,
        gamepadEx: GamepadEx,
        button: GamepadKeys.Button,
): ButtonReader(gamepad = gamepadEx, button = button) {
    operator fun invoke(): Boolean {
        if(wasJustReleased()) {
            currentToggleState = !currentToggleState
        }
        return currentToggleState
    }
}