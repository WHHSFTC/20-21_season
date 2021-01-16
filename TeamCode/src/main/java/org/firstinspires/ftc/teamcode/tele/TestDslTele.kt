package org.firstinspires.ftc.teamcode.tele

import com.acmerobotics.roadrunner.drive.DriveSignal
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.module.DriveConstants
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
                var fieldCentric = false

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
                val turtle = gamepad1.left_trigger > .5 || gamepad1.right_trigger > .5
                val x = (-gamepad1.left_stick_y).toDouble()
                val y = (-gamepad1.left_stick_x).toDouble()
                var omega: Double
                if (fieldCentric) {
                    val head = Vector2d(-gamepad1.right_stick_y.toDouble(), -gamepad1.right_stick_x.toDouble()).rotated(if (bot.alliance == Alliance.BLUE) - PI/2.0 else PI/2.0)
                    val norm = head.norm()
                    val err = (head.angle() - dt.poseEstimate.heading + 4 * PI) % (2 * PI)
                    val sym = if (err > PI) err - 2 * PI else err
                    omega =  sym * if (norm > 0.1) norm else 0.0
                }
                else {
                    omega = (-gamepad1.right_stick_x).toDouble()
                }

                val linearScalar = (x.absoluteValue max y.absoluteValue).pow(2.0)
                val turtleScalar = if (turtle) 3.0 else 1.0

                var twist = Pose2d(
                        x = linearScalar * x,
                        y = linearScalar * y,
                        omega
                ) / turtleScalar

                if (fieldCentric) {
                    val theta = -dt.poseEstimate.heading + if (bot.alliance == Alliance.BLUE) -PI/2.0 else PI/2.0
                    twist = Pose2d(
                            x = twist.x * cos(theta) - twist.y * sin(theta),
                            y = twist.y * cos(theta) + twist.x * sin(theta),
                            omega
                    )
                }
                dt.botTwist = twist

                // offset of pi/4 makes wheels strafe correctly at cardinal and intermediate directions
                val (x_, y_, omega_) = twist
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
                }

                val logLocale = task {
                    dt.update()
                    val p = dt.poseEstimate
                    telemetry.addData("x", p.x)
                    telemetry.addData("y", p.y)
                    telemetry.addData("heading", p.heading)
                    telemetry.addData("battery", bot.dt.batteryVoltageSensor.voltage)
                }

                onLoop {
                    par {
                        +runDriveTrain
                        +runIntake
                        +runWobble
                        +runOutput
                        +logLocale
                        //+onPress(gamepad1::x) {
                            //+cmd {fieldCentric = !fieldCentric; loc.poseEstimate = Pose2d(0.0, 0.0, 0.0) }
                        //}
                        +onPress(gamepad2::x) {
                            +cmd {
                                //dt.poseEstimate = Pose2d(0.0, 0.0, 0.0)
                                dt.turn(.1)
                            }
                            //+go(Pose2d()) {
                                //lineToLinearHeading(Pose2d(0.0, 7.0, 0.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                            //}
                        }
                        +onPress(gamepad1::x) {
                            +cmd {
                                if (gamepad1.right_trigger > .5 || gamepad1.left_trigger > .5)
                                    dt.poseEstimate = Pose2d(0.0, 0.0, 0.0)
                                else if (!(dt.poseEstimate epsilonEquals Pose2d()))
                                    dt.followTrajectory(dt.trajectoryBuilder(dt.poseEstimate).lineToSplineHeading(Pose2d()).build())
                            }
                        }
                        //+onPress(gamepad1::y) {
                            //+cmd {
                                //dt.followTrajectory(dt.trajectoryBuilder(dt.poseEstimate).lineToSplineHeading(Pose2d()).build())
                            //}
                        //}
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