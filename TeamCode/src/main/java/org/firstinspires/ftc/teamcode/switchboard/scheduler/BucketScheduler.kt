package org.firstinspires.ftc.teamcode.switchboard.scheduler

import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

class BucketScheduler(val duration: Time, val table: List<List<HardwareScheduler>>) : HardwareScheduler {
    /*
    A = 0 1 2 3 4 5 6 7 8 9 a b

    B = 0   2   4   6   8   a
    C =   1   3   5   7   9   b

    D = 0       4       8
    E =   1       5       9
    F =     2       6       a
    G =       3       7       b

    table = [
        [A],
        [B, C],
        [D, E, F, G]
    ]
     */

    private var n: Long = 0
    override fun output(all: Boolean) {
        if (all) return table.forEach { it.forEach { it.output(all = true) } }
        val start = Time.now()
        val end = start + duration
        table.forEachIndexed { i, l ->
            val period = 1L shl i
            l.forEachIndexed { j, hw ->
                if (Time.now() > end) {
                    n++
                    return@output
                }

                if ((n - j) and (period - 1L) == 0L) { // n % period == j
                    hw.output()
                }
            }
        }
        n++
    }
}