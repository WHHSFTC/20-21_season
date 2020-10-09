package org.firstinspires.ftc.teamcode.dsl

open class WaitFor(private val condition: () -> Boolean): DslOpMode({
    onPeriodic {
        if (condition()) machine.mode = Mode.NULL
    }
})