package org.firstinspires.ftc.teamcode.cmd

import org.firstinspires.ftc.teamcode.module.Robot

class HeldCommand(
        val condition: Condition,
        val sequential: Boolean,
        val heldCommand: Command,
        var exitCommand: Command,
): Command() {
    private var previous: Boolean = false

    override suspend fun execute(bot: Robot) {
        val current = condition()

        if (current) {
            heldCommand.execute(bot)
        } else if (previous) {
            exitCommand.execute(bot)
        }

        previous = current
    }
}

suspend infix fun HeldCommand.onExit(block: suspend CommandListContext.() -> CommandListContext) = this.apply {
    exitCommand = if (sequential)
        SequentialCommand(CommandListContext().block().build())
    else
        ParallelCommand(CommandListContext().block().build())
}
