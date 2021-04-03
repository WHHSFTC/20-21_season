package org.firstinspires.ftc.teamcode.switchboard.scheduler.dsl

import org.firstinspires.ftc.teamcode.switchboard.scheduler.CyclicalProcess
import org.firstinspires.ftc.teamcode.switchboard.scheduler.Process
import org.firstinspires.ftc.teamcode.switchboard.scheduler.SequentialProcess

fun seq(b: ListBuilderContext<Process>.() -> Unit) = SequentialProcess(ListBuilderContext<Process>().apply(b).build())
fun swap(b: ListBuilderContext<Process>.() -> Unit) = CyclicalProcess(ListBuilderContext<Process>().apply(b).build())

/*
val schedule =
    seq {
        +read
        +odo
        +dt
        +swap {
            +idle
            +swap {
                +squeue
                +arm
                +shooter
            }
        }
    }
 */
