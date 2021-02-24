package org.firstinspires.ftc.teamcode.cmd

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.dsl.RobotDsl

@RobotDsl
object CommandContext: DSLContext()

suspend fun CommandContext.seq(b: suspend CommandListContext.() -> CommandListContext): SequentialCommand =
        SequentialCommand(CommandListContext().b().build())

suspend fun CommandContext.par(b: suspend CommandListContext.() -> CommandListContext): ParallelCommand =
        ParallelCommand(CommandListContext().b().build())

suspend fun CommandListContext.await(res: Long = 100, p: () -> Boolean): Command =
        cmd {
            while (!p()) {
                delay(res)
            };
        }
