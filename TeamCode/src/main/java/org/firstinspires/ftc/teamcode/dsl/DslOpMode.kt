package org.firstinspires.ftc.teamcode.dsl

import org.firstinspires.ftc.teamcode.fsm.Machine
import org.firstinspires.ftc.teamcode.fsm.emptyMachine
import org.firstinspires.ftc.teamcode.module.OpMode

open class DslOpMode(protected var machine: Machine = emptyMachine()): OpMode() {
    constructor(getter: Machine.() -> Unit): this(emptyMachine().apply(getter))
    constructor(context: OpModeContext): this(emptyMachine().apply {
        mode = context.mode
        tasks.putAll(context.tasks)
        fnInit = context.fnInit
        fnPeriodic = context.fnPeriodic
        fnStop = context.fnStop
    })

    init {
        this.bot.machine = machine
    }

    override fun onInit() {
        machine.fnInit(this.bot.apply { context = OpModeContext.Context.INIT })
    }

    override fun onPeriodic() {
        machine.fnPeriodic(
                this.bot.apply {
                    context = if(bot.machine.mode == Mode.AUTO)
                        OpModeContext.Context.RUN
                    else
                        OpModeContext.Context.LOOP
                }
        )
    }

    override fun onStop() {
        machine.fnStop(this.bot.apply { context = OpModeContext.Context.STOP})
    }

    companion object
}