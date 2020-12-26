package org.firstinspires.ftc.teamcode.tele

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.cmd.*
import org.firstinspires.ftc.teamcode.dsl.*
import org.firstinspires.ftc.teamcode.module.*
import kotlin.math.*

@TeleOp
class TestDslTele: DslOpMode() {
    init {
        runBlocking {
            dsl {
                var prevShoot = false
                var prevBurst = false

                infix fun Double.max(other: Double): Double {
                    return this.coerceAtLeast(other)
                }
                onInit {
                    seq {
                        +cmd {
                            log.logData {
                                appendLine("Init")
                                appendLine("...")
                                appendLine("Done")
                            }
                        }
                    }
                }

            val runDriveTrain: Command = task {
                val turtle = gamepad1.left_trigger > .5
                val x = (-gamepad1.left_stick_y).toDouble()
                val y = (-gamepad1.left_stick_x).toDouble()
                val omega = (-gamepad1.right_stick_x).toDouble()

                val linearScalar = (x.absoluteValue max y.absoluteValue).pow(2.0)
                val turtleScalar = if (turtle) 3.0 else 1.0

                bot.dt.twist = Pose2d(
                        x = linearScalar * x,
                        y = linearScalar * y,
                        omega
                ) / turtleScalar

                // offset of pi/4 makes wheels strafe correctly at cardinal and intermediate directions
                val (x_, y_, omega_) = bot.dt.twist
                log.logData("x power: $x_")
                log.logData("y power: $y_")
                log.logData("omega power: $omega_")
            }

                val runWobble = task {
                    when {
                        gamepad1.right_bumper -> wob.claw(Wobble.ClawState.CLOSED)
                        gamepad1.left_bumper -> wob.claw(Wobble.ClawState.OPEN)

                        gamepad1.dpad_up -> wob.elbow(Wobble.ElbowState.CARRY)
                        gamepad1.dpad_right -> wob.elbow(Wobble.ElbowState.STORE)
                        gamepad1.dpad_left -> wob.elbow(Wobble.ElbowState.DROP)
                        gamepad1.dpad_down -> wob.elbow(Wobble.ElbowState.INTAKE)
                    }
                }

                val runIntake = task {
                    when {
                        gamepad1.y -> ink(Intake.Power.OUT)
                        gamepad1.a -> ink(Intake.Power.IN)
                        gamepad1.b -> ink(Intake.Power.OFF)
                    }
                }

                val runOutput = task {
                    if (gamepad2.right_bumper && !prevShoot) {
                        bot.dt.powers = CustomMecanumDrive.Powers(.0, .0, .0, .0)
                        feed.shoot()
                    }
                    prevShoot = gamepad2.right_bumper

                    if (gamepad2.left_bumper && !prevBurst) {
                        bot.dt.powers = CustomMecanumDrive.Powers(.0, .0, .0, .0)
                        feed.burst()
                    }
                    prevBurst = gamepad2.left_bumper

                    when {
                        gamepad2.right_trigger > 0.5 -> {
                            when {
                                gamepad2.dpad_up -> aim.height(HeightController.Height.HIGH)
                                gamepad2.dpad_left || gamepad2.dpad_right -> aim.height(HeightController.Height.POWER)
                                gamepad2.dpad_down -> aim.height(HeightController.Height.ZERO)
                            }
                        }
                        gamepad1.left_trigger > 0.5 -> {
                            aim.power(-gamepad2.right_stick_y.toDouble() * .25)
                        }
                        else -> {
                            when {
                                gamepad2.dpad_up -> feed.height(Indexer.Height.HIGH)
                                gamepad2.dpad_left || gamepad2.dpad_right -> feed.height(Indexer.Height.POWER)
                                gamepad2.dpad_down -> feed.height(Indexer.Height.IN)
                            }
                        }
                    }

                    when {
                        gamepad2.a -> out(Shooter.State.FULL)
                        gamepad2.b -> out(Shooter.State.OFF)
                        gamepad2.y -> feed.shake()
                    }

                    log.logData("aim: ${aim.motor.currentPosition}")
                }

                val logLocale = task {
                    telemetry.addData("x", dt.poseEstimate.x)
                    telemetry.addData("y", dt.poseEstimate.y)
                    telemetry.addData("heading", dt.poseEstimate.heading)
                    if (gamepad1.y)
                        dt.poseEstimate = Pose2d(0.0, 0.0,0.0)
                }

                onLoop {
                    par {
                        +runDriveTrain
                        +runIntake
                        +runWobble
                        +runOutput
                        +logLocale
                    }
                }

                onStop {
                    cmd {
                        bot.dt.powers = CustomMecanumDrive.Powers(.0, .0, .0, .0)
                        log.logData(bot.dt::powers)
                    }
                }
            }

        }
    }
}