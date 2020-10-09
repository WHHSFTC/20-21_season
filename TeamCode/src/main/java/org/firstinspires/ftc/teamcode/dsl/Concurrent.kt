package org.firstinspires.ftc.teamcode.dsl

import org.firstinspires.ftc.teamcode.fsm.Machine

open class Concurrent(
        private val mode_: Mode,
        private val commands: List<Machine> = emptyList()
): DslOpMode(
        {
            mode = mode_

            onInit {
                for (command in commands)
                    command.fnInit(this)
            }

            onPeriodic {
                for (command in commands)
                    command.fnPeriodic(this)
            }

            onStop {
                for (command in commands)
                    command.fnStop(this)
            }
        }
)