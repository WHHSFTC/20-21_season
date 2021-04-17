package org.firstinspires.ftc.teamcode.switchboard.gamepad

import org.firstinspires.ftc.teamcode.switchboard.stores.NullableStore
import org.firstinspires.ftc.teamcode.switchboard.stores.SimpleSubject

class JoystickImpl(
        val xf: () -> Float,
        val yf: () -> Float
) : Gamepad.Joystick, GamepadPart {
    override val x = NullableStore<Double>()
    override val y = NullableStore<Double>()

    override fun same(): Boolean
        = x.value == xf().toDouble()
            && y.value == yf().toDouble()

    override fun updatePart() {
        x.next(xf().toDouble())
        y.next(yf().toDouble())
    }
}