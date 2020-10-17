package org.firstinspires.ftc.teamcode.tele

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.module.DriveTrain
import org.firstinspires.ftc.teamcode.module.Intake
import org.firstinspires.ftc.teamcode.module.OpMode
import org.firstinspires.ftc.teamcode.module.Wobble
import kotlin.math.*

@TeleOp(name = "Summum", group = "Tele")
class TestTele : OpMode() {
    private var prevTurtle = false
    private var turtle = false

    companion object {
        const val DEADZONE = 0.05
    }

    override fun onInit() {
        telemetry.addLine("Init")
        telemetry.addLine("...")
        telemetry.addLine("Done")
        telemetry.update()
    }

    override fun onRun() {}

    override fun onLoop() {
        runDriveTrain()
        runIntake()
        runWobble()
        bot.odo.updatePose()
        logOdo()
        telemetry.update()
    }
    
    override fun onStop() {
        bot.dt.powers = DriveTrain.Powers(.0, .0, .0, .0)
    }

    fun runDriveTrain() {
        if (gamepad1.x && !prevTurtle) turtle = !turtle
        prevTurtle = gamepad1.x
        var xpow = gamepad1.left_stick_x.toDouble()
        var ypow = -gamepad1.left_stick_y.toDouble()
        var zpow = gamepad1.right_stick_x.toDouble()

        val theta = atan2(ypow, xpow) //angle of joystick

        val power = (abs(xpow) max abs(ypow)).pow(2.0) //logarithmic drive

        // ternaries for dead-zone logic
        xpow = if (abs(xpow) > DEADZONE) xpow else 0.0
        ypow = if (abs(ypow) > DEADZONE) ypow else 0.0
        zpow = if (abs(zpow) > DEADZONE) zpow else 0.0

        val zpower = abs(zpow).pow(2.0) / (if (turtle) 3.0 else 1.0)
        val x = cos(theta) / (if (turtle) 3.0 else 1.0)
        val y = sin(theta) / (if (turtle) 3.0 else 1.0)
        val z = sign(zpow)

        bot.dt.powers = DriveTrain.Powers(
                rf = power * -(y - x) + zpower * z,
                lf = power * -(-y - x) + zpower * z,
                lb = power * -(-y + x) + zpower * z,
                rb = power * -(y + x) + zpower * z
        )

        // offset of pi/4 makes wheels strafe correctly at cardinal and intermediate directions
        telemetry.addData("xpow", xpow)
        telemetry.addData("zpow", zpow)
        telemetry.addData("ypow", ypow)
        telemetry.addData("theta", theta)
    }

    fun runWobble() {
        when {
            gamepad1.right_bumper -> bot.wob.claw(Wobble.ClawState.CLOSED)
            gamepad1.left_bumper -> bot.wob.claw(Wobble.ClawState.OPEN)

            gamepad2.dpad_up -> bot.wob.elbow(Wobble.ElbowState.UP)
            gamepad2.dpad_down -> bot.wob.elbow(Wobble.ElbowState.DOWN)
        }
        telemetry.addData("[wob] elbow:", bot.wob.elbow())
        telemetry.addData("[wob] claw:", bot.wob.claw())
    }

    fun runIntake() {
        when {
            gamepad1.y -> bot.ink(Intake.Power.OUT)
            gamepad1.a -> bot.ink(Intake.Power.IN)
            gamepad1.b -> bot.ink(Intake.Power.OFF)
        }
    }

    fun logOdo() {
        bot.log.addData("Pose", bot.odo.position)
    }

    infix fun Double.max(other: Double): Double {
        return this.coerceAtLeast(other)
    }
}

