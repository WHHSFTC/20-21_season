package org.firstinspires.ftc.teamcode.switchboard.event

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Channel<T>(private var data: T) {
    private val subscribers: MutableList<(T) -> Unit> = mutableListOf()

    fun subscribe(handler: (T) -> Unit) {
        subscribers += handler
    }

    fun set(value: T) {
        data = value
        subscribers.forEach { it(value) }
    }

    fun get() = data

    val delegate by lazy {
        object : ReadWriteProperty<Any?, T> {
            override operator fun getValue(t: Any?, p: KProperty<*>): T = get()

            override operator fun setValue(t: Any?, p: KProperty<*>, value: T) {
                set(value)
            }
        }
    }

    // infix fun <H> and(that: Channel<H>): Channel<Pair<T, H>>
}
