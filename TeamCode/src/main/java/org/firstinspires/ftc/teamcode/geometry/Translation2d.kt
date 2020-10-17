package org.firstinspires.ftc.teamcode.geometry

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

    
}