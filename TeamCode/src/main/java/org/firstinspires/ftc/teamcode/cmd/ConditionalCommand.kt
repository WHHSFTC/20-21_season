package org.firstinspires.ftc.teamcode.cmd

import org.firstinspires.ftc.teamcode.module.Robot

class ConditionalCommand(
        val condition: Condition,
        val sequential: Boolean,
        val onTrue: Command,
        val onFalse: Command,
): Command() {
    override suspend fun execute(bot: Robot) {
        if (condition()) {
            onTrue.execute(bot)
        } else {
            onFalse.execute(bot)
        }
    }
}

suspend infix fun ConditionalCommand.orElse(
        elseC: suspend CommandListContext.() -> CommandListContext,
): ConditionalCommand =
        ConditionalCommand(
                condition = condition,
                sequential = sequential,
                onTrue = onTrue,
                onFalse = if(sequential)
                    SequentialCommand(CommandListContext().elseC().build())
                else
                    ParallelCommand(CommandListContext().elseC().build())
        )
