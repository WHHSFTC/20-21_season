package org.firstinspires.ftc.teamcode.tele

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.cmd.*
import org.firstinspires.ftc.teamcode.dsl.*
import org.firstinspires.ftc.teamcode.module.*
import kotlin.math.*

@TeleOp
class TestDslTele: DslOpMode() {
    init {
        dsl {
            var prevTurtle = false
            var turtle = false
            var prevHeightMode = false
            var heightMode = false

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

            val goHome: Command = task {
                val traj = dt.trajectoryBuilder(loc.poseEstimate)
                        .splineTo(Vector2d(0.0, 0.0), 0.0)
                        .build()
                dt.followTrajectory(traj)
            }

            val runDriveTrain: Command = task {
                if (gamepad1.a) {
goHome(this)
                } else {
                    if (gamepad1.x && !prevTurtle) turtle = !turtle
                    prevTurtle = gamepad1.x
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
            }

            val runWobble = task {
                when {
                    //gamepad1.right_bumper -> bot.wob.claw(Wobble.ClawState.CLOSED)
                    //gamepad1.left_bumper -> bot.wob.claw(Wobble.ClawState.OPEN)

                    //gamepad2.dpad_up -> bot.wob.elbow(Wobble.ElbowState.UP)
                    //gamepad2.dpad_left || gamepad2.dpad_right -> bot.wob.elbow(Wobble.ElbowState.OUT)
                    //gamepad2.dpad_down -> bot.wob.elbow(Wobble.ElbowState.INIT)
                }
                //log.logData("[wob] elbow:", bot.wob.elbow())
                //log.logData("[wob] claw:", bot.wob.claw())
            }

            val runIntake = task {
                when {
                    //gamepad1.y -> bot.ink(Intake.Power.OUT)
                    //gamepad1.a -> bot.ink(Intake.Power.IN)
                    //gamepad1.b -> bot.ink(Intake.Power.OFF)
                }
            }

            val runOutput = task {
                if (gamepad2.x && !prevHeightMode) heightMode = !heightMode
                heightMode = gamepad2.x

                if (heightMode) {
                    when {
                        gamepad2.dpad_up -> aim.height(HeightController.Height.POWER)
                        gamepad2.dpad_down -> aim.height(HeightController.Height.HIGH)
                    }
                } else {
                    aim.power(-gamepad2.right_stick_y.toDouble())
                }

                when {
                    gamepad2.y -> out(Shooter.State.FULL)
                    gamepad2.a -> out(Shooter.State.OFF)
                }

                log.logData("aim: ${aim.motor.currentPosition}")
            }

            onLoop {
                seq {
                    +runDriveTrain
                    +runIntake
                    +runWobble
                    +runOutput
                    //+onPress(gamepad1::a and gamepad1::b or !gamepad1::x) {
                        //+cmd {
                            //log.logData("In toggled command")
                        //}
                        //+randomTask
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