package org.firstinspires.ftc.teamcode.dsl

import org.firstinspires.ftc.teamcode.fsm.Machine
import org.firstinspires.ftc.teamcode.fsm.emptyMachine
import org.firstinspires.ftc.teamcode.module.OpMode
import org.firstinspires.ftc.teamcode.module.withContext

open class DslOpMode(protected var machine: Machine = emptyMachine()): OpMode(Mode.TELE) {
    constructor(getter: Machine.() -> Unit): this(emptyMachine().apply(getter))
    constructor(context: OpModeContext): this(emptyMachine().apply {
        mode = context.mode
        tasks.putAll(context.tasks)
        fnInit = context.fnInit
        fnRun = context.fnRun
        fnLoop = context.fnLoop
        fnStop = context.fnStop
    })

    init {
        this.bot.machine = machine
    }

    override fun onInit() {
        machine.fnInit(this.bot.apply { context = OpModeContext.Context.INIT })
    }

    override fun onRun() {
        machine.fnRun(this.bot.withContext(OpModeContext.Context.RUN))
    }
    override fun onLoop() {
        machine.fnLoop(this.bot.withContext(OpModeContext.Context.LOOP))
    }

    override fun onStop() {
        machine.fnStop(this.bot.apply { context = OpModeContext.Context.STOP})
    }

    companion object
}