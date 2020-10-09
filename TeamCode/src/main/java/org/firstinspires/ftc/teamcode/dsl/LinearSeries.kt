package org.firstinspires.ftc.teamcode.dsl

import org.firstinspires.ftc.teamcode.fsm.Machine
import org.firstinspires.ftc.teamcode.module.withContext

open class LinearSeries(private val remainingCommands: MutableList<Machine>): DslOpMode() {
    constructor(vararg commands: Machine): this(mutableListOf(*commands))
    private var currentCommand: OpModeContext? = null
        get() = field ?: remainingCommands.removeFirstOrNull().apply {
            field = this
            this?.fnInit?.invoke(
                    this@LinearSeries.bot.apply { context = OpModeContext.Context.INIT}
            )
        }

    override fun onInit() {}
    override fun onStop() {}

    override fun onPeriodic() {
        val command = currentCommand ?: return

        if (command.mode == Mode.AUTO) {
            command.fnPeriodic(this.bot.withContext(OpModeContext.Context.RUN))
        }
        else {
            while(command.mode == Mode.TELE) {
                command.fnPeriodic(this.bot.withContext(OpModeContext.Context.LOOP))
            }
        }

        currentCommand = null
        command.fnStop(this.bot.withContext(OpModeContext.Context.STOP))
    }
}