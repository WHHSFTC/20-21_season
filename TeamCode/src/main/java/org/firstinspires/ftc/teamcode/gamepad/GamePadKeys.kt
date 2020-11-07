package org.firstinspires.ftc.teamcode.gamepad

import com.qualcomm.robotcore.hardware.Gamepad

sealed class GamePadKeys(protected val gamepad: Gamepad): Keys {
    class XBoxKeys(gamepad: Gamepad): GamePadKeys(gamepad) {
        override val a: Boolean
            get() = gamepad.a
        override val b: Boolean
            get() = gamepad.b
        override val x: Boolean
            get() = gamepad.x
        override val y: Boolean
            get() = gamepad.y
    }

    class PS4Keys(gamepad: Gamepad): GamePadKeys(gamepad) {
        override val a: Boolean
            get() = gamepad.cross
        override val b: Boolean
            get() = gamepad.circle
        override val x: Boolean
            get() = gamepad.square
        override val y: Boolean
            get() = gamepad.triangle
    }

    companion object {
        operator fun invoke(gamepad: Gamepad): GamePadKeys =
                if (gamepad.type() == Gamepad.Type.SONY_PS4)
            PS4Keys(gamepad)
        else
            XBoxKeys(gamepad)
    }
}
