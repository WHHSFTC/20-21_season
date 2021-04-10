package org.firstinspires.ftc.teamcode.switchboard.observe

import org.firstinspires.ftc.teamcode.switchboard.core.Logger
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Channel<T>(private var data: T, val name: String = "Untitled Channel", val stream: Logger.LogStream) : Observable<T>() {
    fun set(value: T) {
        data = value
        stream[name] = value
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
