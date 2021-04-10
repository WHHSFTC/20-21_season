package org.firstinspires.ftc.teamcode.switchboard.shapes

class Distance private constructor(val inches: Double) {
    val ft get() = inches / INCH_IN_FOOT
    val cm get() = inches * CM_IN_INCH
    val mm get() = inches * MM_IN_INCH

    operator fun times(that: Double): Distance = Distance(this.inches * that)
    operator fun div(that: Double): Distance = Distance(this.inches / that)
    operator fun plus(that: Distance): Distance = Distance(this.inches + that.inches)
    operator fun minus(that: Distance): Distance = Distance(this.inches - that.inches)
    operator fun compareTo(that: Distance): Int = this.inches.compareTo(that.inches)

    override fun toString(): String = "$inches inches"

    companion object {
        const val INCH_IN_FOOT = 12.0
        const val CM_IN_INCH = 2.54
        const val MM_IN_CM = 10.0
        const val MM_IN_INCH = MM_IN_CM * CM_IN_INCH

        val zero = Distance(0.0)
        fun ft(ft: Double) = Distance(ft * INCH_IN_FOOT)
        fun inch(inches: Double) = Distance(inches)
        fun mm(mm: Double) = Distance(mm / MM_IN_INCH)
        fun cm(cm: Double) = Distance(cm / CM_IN_INCH)
    }
}
