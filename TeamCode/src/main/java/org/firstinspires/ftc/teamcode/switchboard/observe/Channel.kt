package org.firstinspires.ftc.teamcode.switchboard.observe

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Channel<T>(private var data: T) : Observable<T>() {
    fun set(value: T) {
        data = value
        refresh(value)
    }

    fun get() = data

    val delegate by lazy {
        object : ReadWriteProperty<Any?, T> {
            override operator fun getValue(thisRef: Any?, property: KProperty<*>): T = get()

            override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                set(value)
            }
        }
    }
}
