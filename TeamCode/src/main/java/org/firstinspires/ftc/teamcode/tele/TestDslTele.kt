package org.firstinspires.ftc.teamcode.tele

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.cmd.*
import org.firstinspires.ftc.teamcode.dsl.*
import org.firstinspires.ftc.teamcode.module.DriveTrain
import org.firstinspires.ftc.teamcode.module.Intake
import org.firstinspires.ftc.teamcode.module.Wobble
import kotlin.math.*

@TeleOp
class TestDslTele: DslOpMode() {
    init {
        dsl {
            var prevTurtle = false
            var turtle = false

            infix fun Double.max(other: Double): Double {
                return this.coerceAtLeast(other)
            }

            val DEADZONE = 0.05

            onInit {
                cmd {
                    log.logData("Init")
                    log.logData("...")
                    log.logData("Done")
                }
            }

            val runDriveTrain: Command = task {
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
                log.logData("xpow", xpow)
                log.logData("zpow", zpow)
                log.logData("ypow", ypow)
                log.logData("theta", theta)
            }

            val runWobble = task {
                when {
                    gamepad1.right_bumper -> bot.wob.claw(Wobble.ClawState.CLOSED)
                    gamepad1.left_bumper -> bot.wob.claw(Wobble.ClawState.OPEN)

                    gamepad2.dpad_up -> bot.wob.elbow(Wobble.ElbowState.UP)
                    gamepad2.dpad_left || gamepad2.dpad_right -> bot.wob.elbow(Wobble.ElbowState.OUT)
                    gamepad2.dpad_down -> bot.wob.elbow(Wobble.ElbowState.INIT)
                }
                log.logData("[wob] elbow:", bot.wob.elbow())
                log.logData("[wob] claw:", bot.wob.claw())
            }

            val runIntake = task {
                when {
                    gamepad1.y -> bot.ink(Intake.Power.OUT)
                    gamepad1.a -> bot.ink(Intake.Power.IN)
                    gamepad1.b -> bot.ink(Intake.Power.OFF)
                }
            }

            val randomTask = task {
                log.logData("Some random task")
            }

            onLoop {
                seq {
                    +runDriveTrain
                    +runIntake
                    +runWobble
                    +(condition({turtle}) {
                        +cmd {
                            log.logData("Currently in turtle mode")
                        }
                    } orElse {
                        +cmd {
                            log.logData("Currently not in turtle mode")
                        }
                    })
                    +onPress(gamepad1::a and gamepad1::b or !gamepad1::x) {
                        +cmd {
                            log.logData("In toggled command")
                        }
                        +randomTask
                    }
                }
            }

            onStop {
                cmd {
                    bot.dt.powers = DriveTrain.Powers(.0, .0, .0, .0)
                }
            }
        }
    }
}