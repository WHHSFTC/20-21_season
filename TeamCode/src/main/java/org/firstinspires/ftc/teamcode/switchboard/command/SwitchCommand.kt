package org.firstinspires.ftc.teamcode.switchboard.command

import org.firstinspires.ftc.teamcode.switchboard.core.Frame

class SwitchCommand<T>(val supplier: () -> T, val list: List<Case<T>>) : Command {
    override var done: Boolean = false
    private var cmd: Command? = null

    override fun load(frame: Frame) {
        val v = supplier()
        for (c in list) {
            if (c.pred(v)) {
                cmd = c.command
                return
            }
        }
        cmd = Command.nop
    }

    override fun update(frame: Frame) {
        if (done) return
        val c = cmd ?: return load(frame)
        if (c.done) done = true
        else c.update(frame)
    }

    data class Case<T>(val pred: (T) -> Boolean, val command: LinearCommand)
}