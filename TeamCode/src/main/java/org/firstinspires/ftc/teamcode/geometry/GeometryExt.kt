package org.firstinspires.ftc.teamcode.geometry

infix fun Pose2d.relativeTo(other: Pose2d) = this.relativeTo(other)

infix fun Pose2d.transformBy(other: Transform2d) = this.transformBy(other)

infix fun Pose2d.rotate(deltaTheta: Double) = this.rotate(deltaTheta = deltaTheta)

infix fun Pose2d.exp(other: Twist2d) = this.exp(other)

infix fun Pose2d.log(other: Pose2d) = this.log(other)

operator fun Number.times(other: Rotation2d) = other * this

infix fun Rotation2d.rotateBy(other: Rotation2d) = this.rotateBy(other)

operator fun Number.times(other: Translation2d) =
        other * this.toDouble()

operator fun Number.div(other: Translation2d) =
        other * (1 / this.toDouble())

infix fun Translation2d.rotateBy(other: Rotation2d) = this.rotateBy(other)

infix fun Translation2d.distanceFrom(other: Translation2d) = this.distanceFrom(other)

operator fun Number.times(v: Vector2d): Vector2d = v * this

infix fun Vector2d.rotateBy(angle: Double) = this.rotateBy(angle)

infix fun Vector2d.dot(other: Vector2d) = this.dot(other)

infix fun Vector2d.scalarProject(other: Vector2d) = this.scalarProject(other)

infix fun Vector2d.project(other: Vector2d) = this.project(other)
