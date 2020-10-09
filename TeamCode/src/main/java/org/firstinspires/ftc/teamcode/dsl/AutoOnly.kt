package org.firstinspires.ftc.teamcode.dsl

import org.firstinspires.ftc.teamcode.fsm.Machine

class AutoOnly(command: Machine): DslOpMode(if(command.mode == Mode.AUTO) command else Pass())