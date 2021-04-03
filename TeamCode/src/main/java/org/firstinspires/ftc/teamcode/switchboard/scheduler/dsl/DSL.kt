package org.firstinspires.ftc.teamcode.switchboard.scheduler.dsl

import org.firstinspires.ftc.teamcode.switchboard.scheduler.CyclicalSchedule
import org.firstinspires.ftc.teamcode.switchboard.scheduler.Process
import org.firstinspires.ftc.teamcode.switchboard.scheduler.SequentialSchedule

fun seq(b: ListBuilderContext<Process>.() -> Unit)
        = SequentialSchedule(
            ListBuilderContext<Process>()
                    .apply(b)
                    .build()
        )

fun swap(b: ListBuilderContext<Process>.() -> Unit)
        = CyclicalSchedule(
            ListBuilderContext<Process>()
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
 */
