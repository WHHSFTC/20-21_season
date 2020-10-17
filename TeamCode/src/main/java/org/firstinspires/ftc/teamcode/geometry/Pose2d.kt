package org.firstinspires.ftc.teamcode.geometry

import kotlin.math.*


data class Pose2d(
        val translation: Translation2d,
        val rotation: Rotation2d
) {
    constructor(x: Number, y: Number, rotation: Rotation2d):
            this(Translation2d(x.toDouble(), y.toDouble()), rotation)
    constructor(): this(Translation2d(), Rotation2d())

    val heading: Double
        get() = rotation.radians

    fun transformBy(other: Transform2d): Pose2d =
            this.copy(
                    translation = translation + (other.translation rotateBy rotation),
                    rotation = rotation + other.rotation
            )

    fun relativeTo(other: Pose2d): Pose2d {
        val transform = Transform2d(initial = other, last = this)
        return Pose2d(translation = transform.translation, rotation = other.rotation)
    }

    fun rotate(deltaTheta: Double) =
            this.copy(translation = translation, rotation = Rotation2d(angle = heading + deltaTheta))

    operator fun plus(other: Transform2d) = this transformBy other

    operator fun minus(other: Pose2d): Transform2d {
        val pose = this relativeTo other
        return Transform2d(translation = pose.translation, rotation = pose.rotation)
    }

    fun exp(twist: Twist2d): Pose2d {
        val (dx, dy, dTheta) = twist

        val (sinTheta, cosTheta) = sin(dTheta) to cos(dTheta)

        val (s, c) = if (dTheta.absoluteValue < 1E-9) {
            1.0 - 1.0 / 6.0 * dTheta * dTheta to 0.5 * dTheta
        } else {
            sinTheta / dTheta to (1 - cosTheta) / dTheta
        }
        val transform = Transform2d(Translation2d(x = dx * s - dy * c, y = dx * c + dy * s),
                Rotation2d(x = cosTheta, y = sinTheta))

        return this + transform
    }

    fun log(end: Pose2d): Twist2d {
        val transform: Pose2d = end relativeTo this
        val dTheta: Double = transform.rotation.radians
        val halfDTheta = dTheta / 2.0

        val cosMinusOne: Double = transform.rotation.cos - 1

        val halfThetaByTanOfHalfDTheta: Double
        halfThetaByTanOfHalfDTheta = if (cosMinusOne.absoluteValue < 1E-9) {
            1.0 - 1.0 / 12.0 * dTheta * dTheta
        } else {
            -(halfDTheta * transform.rotation.sin) / cosMinusOne
        }

        val (x, y) =
                (transform.rotation rotateBy Rotation2d(x = halfThetaByTanOfHalfDTheta, y = -halfDTheta)) *
                        (hypot(halfThetaByTanOfHalfDTheta, halfDTheta))

        return Twist2d(dx = x, dy = y, dTheta = dTheta)
    }

    override fun toString(): String =
            "Pose2d($translation, $rotation)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Pose2d) return false

        return translation == other.translation &&
                rotation == other.rotation
    }

    override fun hashCode(): Int {
        var result = translation.hashCode()
        result = 31 * result + rotation.hashCode()
        return result
    }
}
