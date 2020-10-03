package org.firstinspires.ftc.teamcode.module.delegate

import kotlin.reflect.KProperty

class Module<S>(val getter: () -> S, val setter: (S) -> Unit) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): S {
        return getter()
    }
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: S) {
        setter(value)
    }
}