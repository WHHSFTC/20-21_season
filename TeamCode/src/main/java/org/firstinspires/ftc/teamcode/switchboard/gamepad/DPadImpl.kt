package org.firstinspires.ftc.teamcode.switchboard.gamepad

import org.firstinspires.ftc.teamcode.switchboard.stores.NullableStore

class DPadImpl(val gamepad: com.qualcomm.robotcore.hardware.Gamepad) : Gamepad.DPad, GamepadPart {
    override val up = NullableStore<Boolean>()
    override val down = NullableStore<Boolean>()
    override val left = NullableStore<Boolean>()
    override val right = NullableStore<Boolean>()

    override fun same(): Boolean
        = up.value == gamepad.dpad_up
            && down.value == gamepad.dpad_down
            && left.value == gamepad.dpad_left
            && right.value == gamepad.dpad_right

    override fun updatePart() {
        up.next(gamepad.dpad_up)
        down.next(gamepad.dpad_down)
        left.next(gamepad.dpad_left)
        right.next(gamepad.dpad_right)
    }
}