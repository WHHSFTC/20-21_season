package org.firstinspires.ftc.teamcode.gamepad.old

import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.gamepad.Keys

sealed class GamePadKeysOld(protected val gamepad: Gamepad): Keys {
    class XBoxKeys(gamepad: Gamepad): GamePadKeysOld(gamepad) {
        override val a: Boolean
            get() = gamepad.a
        override val b: Boolean
            get() = gamepad.b
        override val x: Boolean
            get() = gamepad.x
        override val y: Boolean
            get() = gamepad.y
    }

    class PS4Keys(gamepad: Gamepad): GamePadKeysOld(gamepad) {
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
        operator fun invoke(FTCGamepad: Gamepad): GamePadKeysOld {
            return if (FTCGamepad.type() == Gamepad.Type.SONY_PS4)
                PS4Keys(FTCGamepad)
            else
                XBoxKeys(FTCGamepad)
        }
    }
}
