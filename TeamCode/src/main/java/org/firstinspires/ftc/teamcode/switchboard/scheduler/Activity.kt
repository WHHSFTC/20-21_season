package org.firstinspires.ftc.teamcode.switchboard.scheduler

import org.firstinspires.ftc.teamcode.switchboard.event.Envelope
import org.firstinspires.ftc.teamcode.switchboard.event.Event
import org.firstinspires.ftc.teamcode.switchboard.shapes.BackingList

abstract class Activity(val queue: BackingList<Envelope>.SharedQueue) : Schedule {
    abstract fun load()
    abstract fun maintain()
    abstract fun update()
    abstract fun cleanup()

    fun handleEvents() {
        while (queue.isNotEmpty()) {
            val envelope = queue.element()
            if (envelope.sender != this && on(envelope))
                break
        }
    }

    fun send(e: Event): Envelope
        = Envelope(e, this, 0, 0).also(queue::add)

    open fun on(e: Envelope): Boolean = false

    override fun recurse(f: (Activity) -> Unit) {
        f(this)
    }
    override fun select(f: (Activity) -> Unit) {
        f(this)
    }

    override fun makeList(): List<Activity> = listOf(this)
 }
