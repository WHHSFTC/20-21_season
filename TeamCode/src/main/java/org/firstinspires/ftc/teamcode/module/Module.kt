package org.firstinspires.ftc.teamcode.module

interface Module<T> {
    var state: T

    operator fun invoke(): T = state

    operator fun invoke(value: T) {
       state = value
    }
}

