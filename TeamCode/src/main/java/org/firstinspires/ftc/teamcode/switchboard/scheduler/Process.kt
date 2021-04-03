package org.firstinspires.ftc.teamcode.switchboard.scheduler

abstract class Process : Schedule {
    abstract fun load()
    abstract fun run()

    override fun recurse(f: (Process) -> Unit) {
        f(this)
    }
    override fun select(f: (Process) -> Unit) {
        f(this)
    }

    object idle : Process() {
        override fun load() {}
        override fun run() {}
    }
}