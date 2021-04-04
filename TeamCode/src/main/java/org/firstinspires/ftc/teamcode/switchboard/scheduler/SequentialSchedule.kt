package org.firstinspires.ftc.teamcode.switchboard.scheduler

class SequentialSchedule(val list: List<Schedule>): Schedule {
    override fun select(f: (Activity) -> Unit) {
        list.forEach { it.select(f) }
    }

    override fun recurse(f: (Activity) -> Unit) {
        list.forEach { it.recurse(f) }
    }

    override fun makeList(): List<Activity> = list.fold(listOf()) { acc, schedule ->  acc + schedule.makeList() }
}