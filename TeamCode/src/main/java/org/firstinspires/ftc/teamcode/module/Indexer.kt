package org.firstinspires.ftc.teamcode.module

import org.firstinspires.ftc.teamcode.switchboard.hardware.Servo
import org.firstinspires.ftc.teamcode.switchboard.command.Command
import org.firstinspires.ftc.teamcode.switchboard.command.makeLinear
import org.firstinspires.ftc.teamcode.switchboard.core.Activity
import org.firstinspires.ftc.teamcode.switchboard.core.Configuration
import org.firstinspires.ftc.teamcode.switchboard.core.Frame
import org.firstinspires.ftc.teamcode.switchboard.core.Logger

class Indexer(config: Configuration, val logger: Logger) : Activity {
    val feedServo: Servo = config.servos["feeder"]
    val heightServo: Servo = config.servos["setter"]

    enum class Height(val pos: Double) {
        //IN(1.0), POWER(.73), HIGH(.87)
        IN(.16), POWER(.41), HIGH(.35)
    }

    enum class Shoot(val pos: Double) {
        PRE(.28), POST(.13)
    }

    var command: Command = Command.stall
        private set

    override fun load() {
        height(Height.IN)
        feed(Shoot.PRE)
    }

    fun shoot() {
        command = makeLinear {
            task { feed(Shoot.POST) }
            delay(150)
            task { feed(Shoot.PRE) }
        }
    }

    fun burst() {
        command = makeLinear {
            repeat(2) {
                task { feed(Shoot.POST) }
                delay(150)
                task { feed(Shoot.PRE) }
                delay(500)
            }

            task { feed(Shoot.POST) }
            delay(150)
            task { feed(Shoot.PRE) }
            delay(150)
            task { height(Height.IN) }
        }
    }

    fun slowBurst() {
        command = makeLinear {
            repeat(2) {
                task { feed(Shoot.POST) }
                delay(150)
                task { feed(Shoot.PRE) }
                delay(1000)
            }

            task { feed(Shoot.POST) }
            delay(150)
            task { feed(Shoot.PRE) }
            delay(250)
            task { height(Height.IN) }
        }
    }

    fun shake() {
        command = makeLinear {
            task { height(Height.POWER) }
            delay(250)
            task { height(Height.IN) }
            delay(250)

            task { height(Height.POWER) }
            delay(250)
            task { height(Height.IN) }
        }
    }

    val feed = object : Module<Shoot> {
        override var state: Shoot = Shoot.PRE
            set(value) {
                feedServo.position = value.pos
                field = value
            }
    }

    val height = object : Module<Height> {
        override var state: Height = Height.IN
            set(value) {
                heightServo.position = value.pos
                field = value
            }
    }

    override fun update(frame: Frame) {
        command.also { logger.out["indexer"] = it }.let {
            if (it.done)
                command = Command.stall
            else
                it.update(frame)
        }
    }
}