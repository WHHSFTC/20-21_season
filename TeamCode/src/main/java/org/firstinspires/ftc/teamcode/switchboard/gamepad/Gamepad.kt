package org.firstinspires.ftc.teamcode.switchboard.gamepad

import org.firstinspires.ftc.teamcode.switchboard.stores.*
import kotlin.math.atan2
import kotlin.math.sqrt

interface Gamepad {
    val keys: Keys
    val dpad: DPad

    val leftStick: Joystick
    val rightStick: Joystick

    val bumpers: Bumpers
    val triggers: Triggers

    val shift: Observable<Boolean> get() = (triggers.leftDown zip triggers.rightDown).map { (l, r) -> l || r }

    interface DPad {
        val up: Observable<Boolean>
        val down: Observable<Boolean>
        val right: Observable<Boolean>
        val left: Observable<Boolean>
    }

    interface Keys: DPad {
        val a: Observable<Boolean>
        val b: Observable<Boolean>
        val x: Observable<Boolean>
        val y: Observable<Boolean>

        val cross get() = a
        val circle get() = b
        val square get() = x
        val triangle get() = y

        override val down get() = a
        override val right get() = b
        override val left get() = x
        override val up get() = y
    }

    interface Joystick {
        val x: Observable<Double>
        val y: Observable<Double>

        val r: Observable<Double> get() = (x zip y).map { (x, y) -> sqrt(x * x + y * y) }
        val theta: Observable<Double> get() = (x zip y).map { (x, y) -> atan2(y, x) }
    }

    interface Bumpers {
        val left: Observable<Boolean>
        val right: Observable<Boolean>
    }

    interface Triggers {
        val left: Observable<Double>
        val right: Observable<Double>

        val leftDown: Observable<Boolean>
        val rightDown: Observable<Boolean>
    }

    fun update()
}