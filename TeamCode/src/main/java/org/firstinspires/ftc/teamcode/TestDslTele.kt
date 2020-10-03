package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.dsl.*
import org.firstinspires.ftc.teamcode.module.enter

@TeleOp
class TestDslTele: DslOpMode(machine = FSM.fsm {
    onInit {
        log.addData("Some Data")
        log.addData {
            "some calculated data"
        }
    }
    onLoop {
        log.addData("Some Data")
        log.addData {
            "some calculated data"
        }
    }
    onRun {
        log.addData("Some Data")
        log.addData {
            "some calculated data"
        }
        enter("A")
    }
    onStop {
        log.addData("Some Data")
        log.addData {
            "some calculated data"
        }
    }
    "A" {
        log.addData("Some Data")
        log.addData {
            "some calculated data"
        }
        "B"
    }
    "B" {
        log.addData("Some Data")
        log.addData {
            "Some calculated data"
        }
        "C"
    }
    "C" {
        log.addData("Ending")
        "End"
    }
})