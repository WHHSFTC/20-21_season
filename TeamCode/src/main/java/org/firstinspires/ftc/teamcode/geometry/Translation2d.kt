package org.firstinspires.ftc.teamcode.geometry

import kotlin.math.absoluteValue
import kotlin.math.hypot

data class Translation2d(val x: Double, val y: Double) {
    fun distanceFrom(other: Translation2d) =
            hypot(other.x - x, other.y - y)

    val norm: Double
        get() = hypot(x, y)

    fun rotateBy(other: Rotation2d): Translation2d =
            Translation2d(
                    x = x * other.cos - y * other.sin,
                    y = x * other.sin + y * other.cos
            )

    operator fun unaryPlus(): Translation2d =
            this.copy(x = +x, y = +y)

    operator fun unaryMinus(): Translation2d =
            this.copy(x = -x, y = -y)

    operator fun plus(other: Translation2d): Translation2d =
            Translation2d(x = x + other.x, y = y + other.y)

    operator fun minus(other: Translation2d): Translation2d =
            this + -other

    operator fun times(scalar: Number): Translation2d =
            this.copy(x = x * scalar.toDouble(), y = y * scalar.toDouble())

    operator fun div(scalar: Number): Translation2d =
            this.copy(x = x / scalar.toDouble(), y = y / scalar.toDouble())

    override fun toString(): String =
            "Translation2d(X: ${x.decimalFormat(2)}, Y: ${y.decimalFormat(2)}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Translation2d) return false

        return (other.x - x).absoluteValue < 1.0E-9
                && (other.y - y).absoluteValue < 1.0E-9
    }

    private fun Double.decimalFormat(precision: Int) =
            "%${precision}f".format(this)
}