package org.firstinspires.ftc.teamcode.geometry

import kotlin.math.absoluteValue

data class Twist2d(
        val dx: Double = 0.0,
        val dy: Double = 0.0,
        val dtheta: Double = 0.0
) {
    override fun toString(): String =
        "Twist2d(dx: $dx, dy: $dy, dtheta: $dtheta)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Twist2d) return false

        return (other.dx - dx).absoluteValue < 1E-9
                && (other.dy - dy).absoluteValue < 1E-9
                && (other.dtheta - dtheta).absoluteValue < 1E-9
    }
}

