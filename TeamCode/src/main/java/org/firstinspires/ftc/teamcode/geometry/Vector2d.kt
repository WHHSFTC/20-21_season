package org.firstinspires.ftc.teamcode.geometry

import kotlin.math.*

data class Vector2d(
        val x: Double = 0.0,
        val y: Double = 0.0
) {
    constructor(other: Vector2d): this(other.x, other.y)

    val angle: Double = atan2(y, x)
    val norm: Double = hypot(x, y)

    fun rotateBy(angle: Double): Vector2d {
        val rad = angle.toRadians()
        val cos = cos(rad)
        val sin = sin(rad)
        return Vector2d(x * cos - y * sin, x * sin + y * cos)
    }

    operator fun plus(other: Vector2d): Vector2d {
        return Vector2d(other.x + x, other.y + y)
    }

    operator fun minus(other: Vector2d): Vector2d {
        return Vector2d(-other.x + x, -other.y + y)
    }

    fun dot(other: Vector2d): Double {
        return other.x * x + other.y * y
    }

    operator fun unaryMinus(): Vector2d {
        return Vector2d(-x, -y)
    }

    operator fun times(scalar: Number): Vector2d {
        return Vector2d(scalar.toDouble() * x, scalar.toDouble() * y)
    }

    operator fun div(scalar: Double): Vector2d {
        return Vector2d(scalar / x, scalar / y)
    }

    fun scalarProject(other: Vector2d): Double {
        return (this.dot(other)) / (other.dot(other))
    }

    fun project(other: Vector2d): Vector2d {
        return other * ((this.dot(other)) / (other.dot(other)))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vector2d) return false

        return (other.x - x).absoluteValue < 1E-9
                && (other.y - y).absoluteValue < 1E-9
    }

    override fun toString(): String {
        return "<$x, $y>"
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + angle.hashCode()
        result = 31 * result + norm.hashCode()
        return result
    }
}

private fun Number.toRadians() =
        Math.toRadians(this.toDouble())

private fun Number.toDegrees() =
        Math.toDegrees(this.toDouble())
