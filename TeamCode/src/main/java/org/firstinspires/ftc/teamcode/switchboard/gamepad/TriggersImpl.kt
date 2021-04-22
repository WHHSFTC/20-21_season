package org.firstinspires.ftc.teamcode.switchboard.gamepad

import org.firstinspires.ftc.teamcode.switchboard.stores.NullableStore
import org.firstinspires.ftc.teamcode.switchboard.stores.map

class TriggersImpl(val gamepad: com.qualcomm.robotcore.hardware.Gamepad, deadZone: Double) : Gamepad.Triggers, GamepadPart {
    override val left = NullableStore<Double>()
    override val right = NullableStore<Double>()

    override val leftDown = left.map { it > deadZone }
    override val rightDown = right.map { it > deadZone }

    override fun same(): Boolean
        = left.value == gamepad.left_trigger.toDouble()
            && right.value == gamepad.right_trigger.toDouble()

    override fun updatePart() {
        left.next(gamepad.left_trigger.toDouble())
        right.next(gamepad.right_trigger.toDouble())
    }
}