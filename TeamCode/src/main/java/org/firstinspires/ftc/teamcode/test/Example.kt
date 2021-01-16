package org.firstinspires.ftc.teamcode.test

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.cmd.*
import org.firstinspires.ftc.teamcode.dsl.DslOpMode
import org.firstinspires.ftc.teamcode.module.*
import org.firstinspires.ftc.teamcode.module.Indexer
import kotlin.math.*

@Disabled
@TeleOp
class ExampleTele: DslOpMode(mode = Mode.TELE) {
    init {
        runBlocking {
            dsl {
                var prevShoot = false
                var prevBurst = false

                infix fun Double.max(other: Double): Double {
                    return this.coerceAtLeast(other)
                } // local utility max function

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
                } // run when init button is pressed

                val runDriveTrain: Command = task {
                    val turtle = gamepad1.left_trigger > .5 || gamepad1.right_trigger > .5
                    val x = (-gamepad1.left_stick_y).toDouble()
                    val y = (-gamepad1.left_stick_x).toDouble()
                    val omega: Double = (-gamepad1.right_stick_x).toDouble()

                    val linearScalar = (x.absoluteValue max y.absoluteValue).pow(2.0)
                    val turtleScalar = if (turtle) 3.0 else 1.0

                    val twist = Pose2d(
                            x = linearScalar * x,
                            y = linearScalar * y,
                            omega
                    ) / turtleScalar

                    dt.botTwist = twist

                    // offset of pi/4 makes wheels strafe correctly at cardinal and intermediate directions
                    val (x_, y_, omega_) = twist
                    log.logData("x power: $x_")
                    log.logData("y power: $y_")
                    log.logData("omega power: $omega_")
                } // run all drive train actions

                val runWobble = task {
                    when {
                        gamepad1.right_bumper -> wob.claw(Wobble.ClawState.CLOSED)
                        gamepad1.left_bumper -> wob.claw(Wobble.ClawState.OPEN)

                        gamepad1.dpad_up -> wob.elbow(Wobble.ElbowState.CARRY)
                        gamepad1.dpad_right -> wob.elbow(Wobble.ElbowState.STORE)
                        gamepad1.dpad_left -> wob.elbow(Wobble.ElbowState.DROP)
                        gamepad1.dpad_down -> wob.elbow(Wobble.ElbowState.INTAKE)
                    }
                } // run all wobble goal arm actions

                val runIntake = task {
                    when {
                        gamepad1.y -> {
                            ink(Intake.Power.OUT)
                            out(Shooter.State.OFF)
                        }
                        gamepad1.a -> {
                            ink(Intake.Power.IN)
                            out(Shooter.State.OFF)
                            wob.elbow(Wobble.ElbowState.RING)
                        }
                        gamepad1.b -> ink(Intake.Power.OFF)
                    }
                } // run all flywheel intake actions

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
                        gamepad2.left_trigger > 0.5 -> {
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
                        gamepad2.a -> {
                            out(Shooter.State.FULL)
                            ink(Intake.Power.OFF)
                        }
                        gamepad2.b -> out(Shooter.State.OFF)
                        gamepad2.y -> {
                            out(Shooter.State.REVERSE)
                            ink(Intake.Power.OFF)
                        }
                    }

                    telemetry.addData("aim",  aim.motor.currentPosition)
                } // run all shooter output actions

                val logLocale = task {
                    dt.update()
                    val p = dt.poseEstimate
                    telemetry.addData("x", p.x)
                    telemetry.addData("y", p.y)
                    telemetry.addData("heading", p.heading)
                    telemetry.addData("battery", bot.dt.batteryVoltageSensor.voltage)
                } // logging current position and battery

                onLoop { // running every loop of the TeleOp period after start button is pressed
                    par { // run every command in parallel
                        +runDriveTrain
                        +runIntake
                        +runWobble
                        +runOutput
                        +logLocale
                        +onPress(gamepad2::x) {
                            +cmd {
                                dt.turn(.1)
                            }
                        }
                        +onPress(gamepad1::x) {
                            +cmd {
                                if (gamepad1.right_trigger > .5 || gamepad1.left_trigger > .5)
                                    dt.poseEstimate = Pose2d(0.0, 0.0, 0.0)
                                else if (!(dt.poseEstimate epsilonEquals Pose2d()))
                                    dt.followTrajectory(dt.trajectoryBuilder(dt.poseEstimate).lineToSplineHeading(Pose2d()).build())
                            }
                        }
                    }
                }
                onStop {
                    cmd {
                        bot.dt.powers = CustomMecanumDrive.Powers(.0, .0, .0, .0)
                        log.logData(bot.dt::powers)
                    }
                } // run when stop button is pressed
            }
        }
    }
}