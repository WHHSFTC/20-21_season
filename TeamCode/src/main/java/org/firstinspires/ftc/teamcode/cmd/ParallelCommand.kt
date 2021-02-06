package org.firstinspires.ftc.teamcode.cmd

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.module.Robot

class ParallelCommand(private val commands: List<Command>) : Command() {
    override suspend fun execute(bot: Robot) {
        runBlocking {
            for (c: Command in commands) {
                launch {
                    c.execute(bot)
                }
            }
        }
    }
}
