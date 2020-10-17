package org.firstinspires.ftc.teamcode.geometry

data class Transform2d(
        val translation: Translation2d,
        val rotation: Rotation2d
) {
    constructor(initial: Pose2d, last: Pose2d): this(
            translation = (last.translation - initial.translation) rotateBy (-initial.rotation),
            rotation = last.rotation - initial.rotation
    )

    operator fun times(scalar: Number): Transform2d =
        Transform2d(translation * scalar.toDouble(), rotation * scalar.toDouble())

    override fun toString(): String =
        "Transform2d(translation: $translation, rotation: $rotation)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Transform2d) return false

        return translation == other.translation
                && rotation == other.rotation
    }

    override fun hashCode(): Int {
        var result = translation.hashCode()
        result = 31 * result + rotation.hashCode()
        return result
    }

    val inverse: Transform2d get() = Transform2d(-translation rotateBy -rotation, -rotation)
}
