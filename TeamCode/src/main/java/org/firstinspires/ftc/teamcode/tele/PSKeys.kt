package org.firstinspires.ftc.teamcode.tele

import com.qualcomm.robotcore.hardware.Gamepad

class PSKeys(val g: Gamepad): Keys {
    override val a: Boolean
        get() = g.cross
    override val b: Boolean
        get() = g.circle
    override val x: Boolean
        get() = g.square
    override val y: Boolean
        get() = g.triangle
}