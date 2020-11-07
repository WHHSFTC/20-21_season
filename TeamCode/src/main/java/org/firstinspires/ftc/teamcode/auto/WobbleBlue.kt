package org.firstinspires.ftc.teamcode.auto

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.cmd.*
import org.firstinspires.ftc.teamcode.dsl.*
import org.firstinspires.ftc.teamcode.module.CustomMecanumDrive

@Autonomous
class WobbleBlue: DslOpMode(mode = Mode.AUTO) {
    init {
        dsl {
            onInit {
                cmd {
                    log.logData("Init")
                    log.logData("...")
                    log.logData("Done")
                }
            }

            onRun {
                seq {
                    +cmd {}
                }
            }

            onStop {
                cmd {
                    bot.dt.powers = CustomMecanumDrive.Powers()
                }
            }
        }
    }
}
