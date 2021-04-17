package org.firstinspires.ftc.teamcode.tele

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.geometry.Pose2d
import org.firstinspires.ftc.teamcode.geometry.Vector2d
import org.firstinspires.ftc.teamcode.geometry.angleWrap
import org.firstinspires.ftc.teamcode.geometry.radToDeg
import org.firstinspires.ftc.teamcode.summum.Drivetrain
import org.firstinspires.ftc.teamcode.summum.Summum
import org.firstinspires.ftc.teamcode.switchboard.core.Activity
import org.firstinspires.ftc.teamcode.switchboard.core.Config
import org.firstinspires.ftc.teamcode.switchboard.core.Logger
import org.firstinspires.ftc.teamcode.switchboard.gamepad.Gamepad
import org.firstinspires.ftc.teamcode.switchboard.gamepad.GamepadImpl
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time
import org.firstinspires.ftc.teamcode.switchboard.stores.*

@TeleOp
class DrivetrainTele : LinearOpMode() {
    lateinit var logger: Logger
    lateinit var config: Config
    lateinit var bot: Summum

    lateinit var g1: Gamepad
    lateinit var g2: Gamepad

    override fun runOpMode() {
        logger = Logger(telemetry, displayErr = true)
        config = Config(hardwareMap, logger)
        bot = Summum(logger, config)
        bot.setup()
        logger.update()

        g1 = GamepadImpl(gamepad1)
        g2 = GamepadImpl(gamepad2)

        val keyMap = KeyMap()

        bot.loadActivity(keyMap)

        waitForStart()
        bot.startTime = Time.now()

        while (opModeIsActive()) {
            val now = Time.now()
            val loopTime = now - bot.startTime!!
            logger.out["Runtime"] = loopTime
            val n = config.frame.value.n.toDouble()
            logger.out["Loop Cycle Time (ms)"] = loopTime.milliseconds / n
            logger.out["Loop Frequency (hz)"] = n / loopTime.seconds

            g1.update()
            g2.update()

            bot.update()
        }

        // stop
        bot.cleanup()
        logger.update()
    }

    inner class KeyMap: Activity {
        val translational = (g1.leftStick.x zip g1.leftStick.y).map { (x, y) -> Vector2d(-y, -x) }

        val flick = ((bot.loc.pose to Pose2d()) alt ((g1.rightStick.theta zip g1.rightStick.r) to (0.0 to 0.0))).map { (pose, polar) ->
            0.1 * polar.second * (-polar.first - pose.theta + 90.0.radToDeg()).angleWrap()
        }.comment("Flick Turn")

        val simpleTurn = g1.rightStick.x.map { it * -.25 }.comment("Simple Turn")

        val turnMethod = (g1.shift.posEdge().map { flick } merge g1.shift.negEdge().map { simpleTurn }).taplog(logger.out, "Turn Method")

        val rotational = turnMethod.flatten()

        val twist = ((translational to Vector2d()) alt (rotational to 0.0)).map { (t, r) -> Pose2d(t, r) }
        val accel = PullObservable { Pose2d() }

        val global = ((twist to Pose2d()) alt (accel to Pose2d())).map { (tw, ac) -> Drivetrain.Signal(tw, ac) }
        val local = ((bot.loc.pose to Pose2d()) alt (global to Drivetrain.Signal()))
                .map { (pose, glob) -> Drivetrain.globalToLocal(pose, glob) }

        val follower = g1.keys.a.posEdge().map { global } merge g1.keys.b.posEdge().map { local } merge g1.keys.x.posEdge().map { bot.dt.makeApproach(Vector2d(0.0, 0.0)) }

        override fun load() {
            follower bind bot.dt.follower
        }
    }
}