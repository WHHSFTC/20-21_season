package org.firstinspires.ftc.teamcode.switchboard.scheduler

abstract class Process {
    abstract fun load()
    abstract fun run()

    object idle : Process() {
        override fun load() {}
        override fun run() {}
    }
}