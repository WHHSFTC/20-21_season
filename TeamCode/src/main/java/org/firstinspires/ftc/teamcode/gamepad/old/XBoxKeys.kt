package org.firstinspires.ftc.teamcode.gamepad.old

import com.qualcomm.robotcore.hardware.Gamepad

class XBoxKeys(val g: Gamepad): Keys {
    override val a: Boolean
        get() = g.a
    override val b: Boolean
        get() = g.b
    override val x: Boolean
        get() = g.x
    override val y: Boolean
        get() = g.y
}