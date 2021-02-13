package org.firstinspires.ftc.teamcode.module

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.cmd.Command
import org.firstinspires.ftc.teamcode.dsl.Context
import org.firstinspires.ftc.teamcode.dsl.RobotDsl
import org.firstinspires.ftc.teamcode.module.vision.PipelineRunner
import org.firstinspires.ftc.teamcode.module.vision.StackPipeline

@RobotDsl
class Robot(
        val opMode: OpMode,
): Context<Command.Context> {
    val log: Telemetry = opMode.telemetry
    val hwmap: HardwareMap = opMode.hardwareMap
    val feed: Indexer = Indexer(this)
    val wob: Wobble = Wobble(this)
    //lateinit var wob: Wobble
    val enc: Encoders = Encoders(this)
    val odo: HolonomicOdometry = HolonomicOdometry(enc, TRACK_WIDTH, CENTER_OFFSET)
    val loc: CustomLocalizer = CustomLocalizer(enc)
    //    val dt: DriveTrain = DriveTrain(this)
    val dt: CustomMecanumDrive = CustomMecanumDrive(this)
    val ink: Intake = Intake(this)
    val aim: HeightController = HeightController(this)
    var vis: PipelineRunner<StackPipeline>?
    var out: Shooter = Shooter(this)
    val alliance: Alliance = Alliance.BLUE

    init {
        if (opMode.mode == OpMode.Mode.AUTO)
            vis = PipelineRunner(this)
        else
            vis = null

        //dt.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
    }

    override var context: Command.Context = Command.Context.BASE
        set(value) {
            log.addData("$field", "Switching to $value context")
            field = value
        }

    fun <T: Any> Telemetry.logData(value: T) {
        this.addData("$context", value)
    }

    fun <T: Any, E: Any> Telemetry.logData(caption: T, value: E) {
        this.addData("$context", "$caption: $value")
    }

    fun <T: Any> Telemetry.logData(valueProducer: () -> T) {
        this.addData("$context", valueProducer)
    }

    fun Telemetry.logData(builder: StringBuilder.() -> StringBuilder) {
        this.addData("$context", StringBuilder("").builder().toString())
    }

    fun <T: Any> Telemetry.logError(errorMsg: T) {
        this.addData("[ERROR]", "$errorMsg")
    }

    companion object {
        const val TRACK_WIDTH = 14.5 // TODO
        const val CENTER_OFFSET = 7 // TODO
    }
}

fun Robot.withContext(context_: Command.Context): Robot =
        this.apply { context = context_ }