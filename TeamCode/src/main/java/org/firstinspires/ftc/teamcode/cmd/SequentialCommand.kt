package org.firstinspires.ftc.teamcode.cmd

import org.firstinspires.ftc.teamcode.module.Robot

class SequentialCommand(private val commands: List<Command>) : Command() {
    override suspend fun execute(bot: Robot) {
        for (c: Command in commands) {
            c.execute(bot)
        }
    }
}
