package org.firstinspires.ftc.teamcode.switchboard.stores

fun interface Observer<T> {
    fun next(x: T): Unit
}