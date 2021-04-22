package org.firstinspires.ftc.teamcode.switchboard.gamepad

import org.firstinspires.ftc.teamcode.switchboard.stores.NullableStore

class BumpersImpl(val gamepad: com.qualcomm.robotcore.hardware.Gamepad) : Gamepad.Bumpers, GamepadPart {
    override val left = NullableStore<Boolean>()
    override val right = NullableStore<Boolean>()

    override fun same(): Boolean
        = left.value == gamepad.left_bumper
            && right.value == gamepad.right_bumper

    override fun updatePart() {
        left.next(gamepad.left_bumper)
        right.next(gamepad.right_bumper)
    }
}