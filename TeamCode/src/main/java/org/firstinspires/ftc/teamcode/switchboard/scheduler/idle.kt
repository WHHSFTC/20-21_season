package org.firstinspires.ftc.teamcode.switchboard.scheduler

object idle : Schedule {
    override fun recurse(f: (Activity) -> Unit) { }
    override fun select(f: (Activity) -> Unit) { }

    override fun makeList(): List<Activity> = listOf()
}