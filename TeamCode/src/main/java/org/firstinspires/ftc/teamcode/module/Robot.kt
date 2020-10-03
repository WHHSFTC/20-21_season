package org.firstinspires.ftc.teamcode.module

import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.dsl.Context
import org.firstinspires.ftc.teamcode.dsl.OpModeContext
import org.firstinspires.ftc.teamcode.dsl.RobotDsl
import org.firstinspires.ftc.teamcode.fsm.Machine

@RobotDsl
open class Robot(
        val opMode: OpMode,
        var machine: Machine,
): Context<OpModeContext.Context> {
    val dt: DriveTrain = DriveTrain(opMode)
    val log: Telemetry = opMode.telemetry

    override var context: OpModeContext.Context = OpModeContext.Context.BASE
        set(value) {
            opMode.telemetry.addData("$field", "Switching to $value context")
            field = value
        }

    fun <T: Any> Telemetry.addData(value: T) {
        this.addData("$context", value)
    }

    fun <T: Any> Telemetry.addData(valueProducer: () -> T) {
        this.addData("$context", valueProducer)
    }
}

fun Robot.enter(initialKey: String) =
    if (machine.isNotEmpty()) {
        log.addData("State Machine: Running $initialKey")
        var state: (Robot.() -> String)? = machine.map[initialKey]
        var key: String = initialKey
        while (state != null) {
            key = state()
            log.addData("State Machine: Running $key")
            state = machine.map[key]
        }
        log.addData("State Machine: Exited at $key")
    } else {
        log.addData("Machine is empty, no states detected")
    }
