package org.firstinspires.ftc.teamcode.switchboard.scheduler.dsl

import org.firstinspires.ftc.teamcode.switchboard.scheduler.CyclicalSchedule
import org.firstinspires.ftc.teamcode.switchboard.scheduler.Schedule
import org.firstinspires.ftc.teamcode.switchboard.scheduler.SequentialSchedule
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

fun seq(b: ListBuilderContext<Schedule>.() -> Unit)
        = SequentialSchedule(
            ListBuilderContext<Schedule>()
                    .apply(b)
                    .build()
        )

fun swap(limit: Time = Time.milli(2), b: ListBuilderContext<Schedule>.() -> Unit)
        = CyclicalSchedule(limit,
            ListBuilderContext<Schedule>()
                    .apply(b)
                    .build()
        )

/*
val schedule =
    seq {
        +read // 2ms
        +odo
        +dt // 8ms
        +swap {
            +idle
            +swap {
                +squeue // 2ms
                +arm // 2ms
                +shooter // 4ms
                +leadscrew // 4ms
            }
        }
    }

val activities = listOf(read, odo, dt, hw)
 */
