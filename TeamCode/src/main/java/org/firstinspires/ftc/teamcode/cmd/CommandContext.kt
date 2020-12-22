package org.firstinspires.ftc.teamcode.cmd

import org.firstinspires.ftc.teamcode.dsl.RobotDsl

@RobotDsl
object CommandContext: DSLContext()

suspend fun CommandContext.seq(b: suspend CommandListContext.() -> CommandListContext): SequentialCommand =
        SequentialCommand(CommandListContext().b().build())

suspend fun CommandContext.par(b: suspend CommandListContext.() -> CommandListContext): ParallelCommand =
        ParallelCommand(CommandListContext().b().build())
