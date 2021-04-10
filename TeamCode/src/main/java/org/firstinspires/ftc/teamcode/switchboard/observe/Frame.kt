package org.firstinspires.ftc.teamcode.switchboard.observe

import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

data class Frame(val n: Long, val time: Time, val step: Time) {
    companion object {
        fun from(last: Frame)
            = Time.now().let {
                Frame(
                    last.n + 1,
                    it,
                    it - last.time,
                )
            }
    }
}
