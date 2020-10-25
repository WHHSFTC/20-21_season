package org.firstinspires.ftc.teamcode.auto

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.cmd.*
import org.firstinspires.ftc.teamcode.dsl.*
import org.firstinspires.ftc.teamcode.module.DriveTrain
import org.firstinspires.ftc.teamcode.module.Intake
import org.firstinspires.ftc.teamcode.module.Wobble
import kotlin.math.*


@Config
object TimeAutoVars {
    @JvmField var INCHES_PER_SECOND = 19.3846154
}

@Autonomous
class TimeAuto: DslOpMode() {
    init {
        dsl {
            onInit {
                cmd {
                    log.logData("Init")
                    log.logData("...")
                    log.logData("Done")
                }
            }

            fun moveRight(distance: Double): Command {
                return task {
                    bot.dt.powers = DriveTrain.Powers(.333, .333, -.333, -.333)
                    sleep((distance / TimeAutoVars.INCHES_PER_SECOND * 1000).toLong())
                    bot.dt.powers = DriveTrain.Powers()
                }
            }

            fun moveLeft(distance: Double): Command {
                return task {
                    bot.dt.powers = DriveTrain.Powers(-.333, -.333, .333, .333)
                    sleep((distance / TimeAutoVars.INCHES_PER_SECOND * 1000).toLong())
                    bot.dt.powers = DriveTrain.Powers()
                }
            }

            fun moveForward(distance: Double): Command {
                return task {
                    bot.dt.powers = DriveTrain.Powers(.333, -.333, .333, -.333)
                    sleep((distance / TimeAutoVars.INCHES_PER_SECOND * 1000).toLong())
                    bot.dt.powers = DriveTrain.Powers()
                }
            }

            fun moveBack(distance: Double): Command {
                return task {
                    bot.dt.powers = DriveTrain.Powers(-.333, .333, -.333, .333)
                    sleep((distance / TimeAutoVars.INCHES_PER_SECOND * 1000).toLong())
                    bot.dt.powers = DriveTrain.Powers()
                }
            }

            onRun {
                seq {
                    +moveRight(24.0)
                    +moveForward(66.0)
                    // shoot
                    +moveBack(48.0)
                    +moveLeft(24.0)
                    +moveForward(12.0)
                    // intake
                    +moveForward(36.0)
                    // shoot
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