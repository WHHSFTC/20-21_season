package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.dsl.*

@TeleOp
class TestDslTele: DslOpMode(command = opMode {
    onInit {
        telem.addData("Some Data")
        telem.addData {
            "some calculated data"
        }
    }
    onLoop {
        telem.addData("Some Data")
        telem.addData {
            "some calculated data"
        }
    }
    onRun {
        telem.addData("Some Data")
        telem.addData {
            "some calculated data"
        }
    }
    onStop {
        telem.addData("Some Data")
        telem.addData {
            "some calculated data"
        }
    }
})