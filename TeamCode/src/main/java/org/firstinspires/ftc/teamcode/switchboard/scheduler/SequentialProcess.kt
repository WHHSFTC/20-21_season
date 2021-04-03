package org.firstinspires.ftc.teamcode.switchboard.scheduler

class SequentialProcess(val list: List<Process>) : Process() {
    override fun load() {

    }

    override fun run() {
        list.forEach { it.run() }
    }
}