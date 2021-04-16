package org.firstinspires.ftc.teamcode.switchboard.shapes

class Time private constructor(val nanoseconds: Long) {
    val milliseconds: Double get() = nanoseconds / NANO_IN_MILLI
    val seconds: Double get() = nanoseconds / NANO_IN_SECOND

    operator fun times(that: Int): Time = Time(this.nanoseconds * that)
    operator fun div(that: Int): Time = Time(this.nanoseconds / that)
    operator fun plus(that: Time): Time = Time(this.nanoseconds + that.nanoseconds)
    operator fun minus(that: Time): Time = Time(this.nanoseconds - that.nanoseconds)
    operator fun compareTo(that: Time): Int = this.nanoseconds.compareTo(that.nanoseconds)

    override fun toString(): String = "$seconds s"

    companion object {
        const val NANO_IN_MILLI = 1_000_000.0
        const val MILLI_IN_SECOND = 1_000.0
        const val NANO_IN_SECOND = NANO_IN_MILLI * MILLI_IN_SECOND

        val zero = Time(0)
        fun now() = Time(System.nanoTime())
        fun nano(ns: Long) = Time(ns)
        fun milli(ms: Long) = Time((ms * NANO_IN_MILLI).toLong())
        fun seconds(s: Long) = Time((s * NANO_IN_SECOND).toLong())
    }
}
