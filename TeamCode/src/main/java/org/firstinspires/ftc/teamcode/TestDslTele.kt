package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.dsl.*
import org.firstinspires.ftc.teamcode.module.DriveTrain
import org.firstinspires.ftc.teamcode.module.Intake
import org.firstinspires.ftc.teamcode.module.execute
import kotlin.math.*

@TeleOp
class TestDslTele: DslOpMode() {
    private var heightCounter: Int = 0
    private var prevTurtle = false
    private var turtle = false

    init {
        machine = fsm {
            infix fun Double.max(other: Double): Double {
                return this.coerceAtLeast(other)
            }
            infix fun Int.clip(other: IntRange): Int {
                return min(max(other.first, heightCounter), other.last)
            }
            val DEADZONE = 0.05
            onInit {
                log.logData("Init")
                log.logData("...")
                log.logData("Done")
            }

            task("runDriveTrain") {
                if (gamepad1.x && !prevTurtle) turtle = !turtle
                prevTurtle = gamepad1.x
                var xpow = gamepad1.left_stick_x.toDouble()
                var ypow = -gamepad1.left_stick_y.toDouble()
                var zpow = gamepad1.right_stick_x.toDouble()

                val theta = atan2(ypow, xpow) //angle of joystick

                val power = (abs(xpow) max abs(ypow)).pow(2.0) //logarithmic drive

                // ternaries for dead-zone logic
                xpow = if (abs(xpow) > TestTele.DEADZONE) xpow else 0.0
                ypow = if (abs(ypow) > TestTele.DEADZONE) ypow else 0.0
                zpow = if (abs(zpow) > TestTele.DEADZONE) zpow else 0.0

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
            task("runIntake") {
                when {
                    gamepad1.y -> bot.ink.power = Intake.Power.OUT
                    gamepad1.a -> bot.ink.power = Intake.Power.IN
                    gamepad1.b -> bot.ink.power = Intake.Power.OFF
                }
            }

            onLoop {
                execute("runDriveTrain", "runIntake")
            }

            onStop {
                bot.dt.powers = DriveTrain.Powers(.0, .0, .0, .0)
            }
        }
    }
}