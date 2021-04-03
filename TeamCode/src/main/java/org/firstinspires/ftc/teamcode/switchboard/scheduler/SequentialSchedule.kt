package org.firstinspires.ftc.teamcode.switchboard.scheduler

class SequentialSchedule(val list: List<Process>): Schedule {
    override fun select(f: (Process) -> Unit) {
        list.forEach(f)
    }

    override fun recurse(f: (Process) -> Unit) {
        list.forEach(f)
    }
}