package org.firstinspires.ftc.teamcode.cmd

import org.firstinspires.ftc.teamcode.module.Robot

class ToggleCommand(
        val condition: Condition,
        val sequential: Boolean,
        val toggleCommand: Command,
): Command() {
    private var previous: Boolean = false
    private var toggleState: Boolean = false

    override suspend fun execute(bot: Robot) {
        val current = condition()
        toggleState = if(!previous && current) !toggleState else toggleState

        if(toggleState) {
            toggleCommand.execute(bot)
        }

        previous = current
    }
}
