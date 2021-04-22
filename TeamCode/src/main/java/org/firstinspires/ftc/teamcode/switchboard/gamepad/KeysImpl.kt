package org.firstinspires.ftc.teamcode.switchboard.gamepad

import org.firstinspires.ftc.teamcode.switchboard.stores.NullableStore

class KeysImpl(val gamepad: com.qualcomm.robotcore.hardware.Gamepad) : Gamepad.Keys, GamepadPart {
    override val a = NullableStore<Boolean>()
    override val b = NullableStore<Boolean>()
    override val x = NullableStore<Boolean>()
    override val y = NullableStore<Boolean>()

    override fun same(): Boolean
        = a.value == gamepad.a
            && b.value == gamepad.b
            && x.value == gamepad.x
            && y.value == gamepad.y

    override fun updatePart() {
        a.next(gamepad.a)
        b.next(gamepad.b)
        x.next(gamepad.x)
        y.next(gamepad.y)
    }
}