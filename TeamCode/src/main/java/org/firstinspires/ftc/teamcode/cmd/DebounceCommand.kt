package org.firstinspires.ftc.teamcode.cmd

import org.firstinspires.ftc.teamcode.module.Robot

class DebounceCommand(
        val condition: Condition,
        val sequential: Boolean,
        val command: Command,
): Command() {
    private var previous: Boolean = false

    override suspend fun execute(bot: Robot) {
        val current = condition()

        if (!previous && current) {
            command.execute(bot)
        }

        previous = current
    }
}
