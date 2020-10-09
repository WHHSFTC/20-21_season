package org.firstinspires.ftc.teamcode.module

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.dsl.Context
import org.firstinspires.ftc.teamcode.dsl.OpModeContext
import org.firstinspires.ftc.teamcode.dsl.RobotDsl
import org.firstinspires.ftc.teamcode.fsm.Machine
import org.firstinspires.ftc.teamcode.fsm.State

@RobotDsl
class Robot(
        val opMode: OpMode,
        var machine: Machine,
): Context<OpModeContext.Context> {
    val log: Telemetry = opMode.telemetry
    val hwmap: HardwareMap = opMode.hardwareMap
    val dt: DriveTrain = DriveTrain(this)
    val ink: Intake = Intake(this)

    override var context: OpModeContext.Context = OpModeContext.Context.BASE
        set(value) {
            log.addData("$field", "Switching to $value context")
            field = value
        }

    fun <T: Any> Telemetry.logData(value: T) {
        this.addData("$context", value)
    }

    fun <T: Any, E: Any> Telemetry.logData(caption: T, value: E) {
        this.addData("$context", "$caption: $value")
    }

    fun <T: Any> Telemetry.logData(valueProducer: () -> T) {
        this.addData("$context", valueProducer)
    }

    fun <T: Any> Telemetry.logError(errorMsg: T) {
        this.addData("[ERROR]", "$errorMsg")
    }

    companion object {}
}

fun Robot.enter(initialKey: State) =
        if (machine.map.isNotEmpty()) {
            log.logData("State Machine", "Running $initialKey")
            initialKey(this)
            var state: (Robot.() -> State)? = machine.map[initialKey]
            var key: State = initialKey
            while (state != null) {
                key = state()
                log.logData("State Machine", "Running $key")
                state = machine.map[key]
            }
            log.logData("State Machine", "Exited at $key")
        } else {
            log.logData("State Machine", "No states detected, machine is empty")
        }

fun Robot.execute(taskName: String) =
        if (machine.tasks.isNotEmpty()) {
            log.logData("Task Manager", "Starting $taskName")
            val task: (Robot.() -> Unit)? = machine.tasks[taskName]
            if (task != null) {
                this.task()
            } else {
                log.logData("Task Manager", "Task $taskName not found")
            }
        } else {
            log.logData("Task Manager", "No tasks detected, manager is empty")
        }

fun Robot.execute(vararg taskNames: String) {
    for(taskName in taskNames) {
        this.execute(taskName)
    }
}

fun Robot.executeAllTasks() {
    for (task in machine.tasks.keys) {
        this.execute(task)
    }
}

fun Robot.withContext(context_: OpModeContext.Context): Robot =
        this.apply { context = context_ }