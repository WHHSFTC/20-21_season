package org.firstinspires.ftc.teamcode.module

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.module.vision.PipelineRunner

class Robot(val opMode: OpMode) {
    val log: Telemetry = opMode.telemetry
    val hwmap: HardwareMap = opMode.hardwareMap
    val feed: Indexer = Indexer(this)
    val wob: Wobble = Wobble(this)
    val enc: Encoders = Encoders(this)
    val loc: CustomLocalizer = CustomLocalizer(enc)
    val dt: CustomMecanumDrive = CustomMecanumDrive(this)
    val ink: Intake = Intake(this)
    val aim: HeightController = HeightController(this)
    var vis: PipelineRunner?
    var out: Shooter = Shooter(this)
    val alliance: Alliance = Alliance.BLUE

    init {
        if (opMode.mode == OpMode.Mode.AUTO) {
            vis = PipelineRunner(this, 640, 480)
            vis!!.load(vis!!.stack)
            vis!!.start()
        } else {
            vis = null
        }

        //dt.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
    }
    companion object {
        const val TRACK_WIDTH = 14.5 // TODO
        const val CENTER_OFFSET = 7 // TODO
        const val WIDTH = 17.375
        const val LENGTH = 17.375
    }
}

