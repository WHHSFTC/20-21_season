package org.firstinspires.ftc.teamcode.geometry

import kotlin.math.PI
import kotlin.math.abs

const val TAU = 2.0 * PI

fun Number.angleWrap(): Double {
    var d = this.toDouble()

    // val zeroToTau = (d % TAU + TAU) % TAU

    while (d > PI) d -= TAU
    while (d < -PI) d += TAU
    return d
}

fun Number.rad() =
        Math.toRadians(this.toDouble())

fun Number.deg() =
        Math.toDegrees(this.toDouble())

fun Number.sin() =
        kotlin.math.sin(this.toDouble())

fun Number.cos() =
        kotlin.math.cos(this.toDouble())

const val EPSILON = 1e-6

infix fun Double.epsilonEquals(other: Double) = abs(this - other) < EPSILON
