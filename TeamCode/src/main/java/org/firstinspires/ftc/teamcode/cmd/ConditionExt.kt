package org.firstinspires.ftc.teamcode.cmd

typealias Condition = () -> Boolean

infix fun Condition.and(other: Condition): Condition = { this() && other() }

infix fun Condition.or(other: Condition): Condition = { this() || other() }

infix fun Condition.xor(other: Condition): Condition = { this() xor other() }

operator fun Condition.not(): Condition = { !this() }
