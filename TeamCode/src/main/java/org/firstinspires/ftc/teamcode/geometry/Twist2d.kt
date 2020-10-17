package org.firstinspires.ftc.teamcode.geometry

import kotlin.math.absoluteValue

data class Twist2d(
        val dx: Double = 0.0,
        val dy: Double = 0.0,
        val dTheta: Double = 0.0
) {
    override fun toString(): String =
        "Twist2d(dx: $dx, dy: $dy, dtheta: $dTheta)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Twist2d) return false

        return (other.dx - dx).absoluteValue < 1E-9
                && (other.dy - dy).absoluteValue < 1E-9
                && (other.dTheta - dTheta).absoluteValue < 1E-9
    }

    override fun hashCode(): Int {
        var result = dx.hashCode()
        result = 31 * result + dy.hashCode()
        result = 31 * result + dTheta.hashCode()
        return result
    }
}

