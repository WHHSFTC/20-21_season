package org.firstinspires.ftc.teamcode.util

sealed class Time(val milliSeconds: Long)

data class MilliSeconds(val value: Long): Time(value)

data class Seconds(val value: Long): Time(value * 1000)

val Number.seconds
        get() = Seconds(this.toLong())

val Number.milliseconds
    get() = MilliSeconds(this.toLong())
