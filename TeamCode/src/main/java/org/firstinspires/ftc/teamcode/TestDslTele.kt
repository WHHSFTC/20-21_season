package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.dsl.*

@TeleOp
class TestDslTele: DslOpMode(command = opMode {
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
    }
    onStop {
        log.addData("Some Data")
        log.addData {
            "some calculated data"
        }
    }
})