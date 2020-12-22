package org.firstinspires.ftc.teamcode.cmd

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

suspend infix fun HeldCommand.onExit(block: suspend CommandListContext.() -> CommandListContext) = this.apply {
    exitCommand = if (sequential)
        SequentialCommand(CommandListContext().block().build())
    else
        ParallelCommand(CommandListContext().block().build())
}
