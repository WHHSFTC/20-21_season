package org.firstinspires.ftc.teamcode.cmd

import org.firstinspires.ftc.teamcode.dsl.RobotDsl
//import kotlin.contracts.ExperimentalContracts
//import kotlin.contracts.InvocationKind
//import kotlin.contracts.contract

@RobotDsl
object CommandContext: DSLContext()

suspend fun CommandContext.seq(b: suspend CommandListContext.() -> Unit): SequentialCommand =
        SequentialCommand(CommandListContext().applySuspending(b).build())

suspend fun CommandContext.par(b: suspend CommandListContext.() -> Unit): ParallelCommand =
        ParallelCommand(CommandListContext().applySuspending(b).build())

//@ExperimentalContracts
private suspend inline fun <T> T.applySuspending(crossinline block: suspend T.() -> Unit): T {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//    }
    this.block()
    return this
}