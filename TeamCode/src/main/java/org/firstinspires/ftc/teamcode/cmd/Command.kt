package org.firstinspires.ftc.teamcode.cmd

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.module.Robot

abstract class Command() {
    abstract fun execute(bot: Robot)
    operator fun invoke(bot: Robot) {
        execute(bot)
    }
}

class EmptyCommand : Command() {
    override fun execute(bot: Robot) {

    }
}

class LambdaCommand(val f: Robot.() -> Unit) : Command() {
    override fun execute(bot: Robot) {
        bot.f()
    }
}

class SequentialCommand(val commands: List<Command>) : Command() {
    override fun execute(bot: Robot) {
        for (c: Command in commands) {
            c.execute(bot)
        }
    }
}

class ParallelCommand(val commands: List<Command>) : Command() {
    override fun execute(bot: Robot) {
        runBlocking {
            for (c: Command in commands) {
                launch { c.execute(bot) }
            }
        }
    }
}

open class DSLContext {
    fun cmd(exec: Robot.() -> Unit): LambdaCommand {
        return LambdaCommand(exec)
    }

    fun seq(b: ListContext.() -> ListContext): SequentialCommand {
        return SequentialCommand(ListContext().b().build())
    }

    fun par(b: ListContext.() -> ListContext): ParallelCommand {
        return ParallelCommand(ListContext().b().build())
    }

    fun empty(): EmptyCommand {
        return EmptyCommand()
    }
}

class ListContext: DSLContext() {
    val commands: MutableList<Command> = mutableListOf()
    fun build(): List<Command> {
        return commands
    }
    operator fun Command.unaryPlus(): ListContext {
        commands += this
        return this@ListContext
    }
}

fun dsl(b: DSLContext.() -> Command): Command {
    return DSLContext().b()
}