package org.firstinspires.ftc.teamcode.module

import org.firstinspires.ftc.teamcode.switchboard.hardware.Servo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.firstinspires.ftc.teamcode.switchboard.command.Command
import org.firstinspires.ftc.teamcode.switchboard.command.linear
import org.firstinspires.ftc.teamcode.switchboard.core.Activity
import org.firstinspires.ftc.teamcode.switchboard.core.Configuration
import org.firstinspires.ftc.teamcode.switchboard.core.Frame

class Indexer(config: Configuration) : Activity {
    val feedServo: Servo = config.servos["feeder"]
    val heightServo: Servo = config.servos["setter"]

    enum class Height(val pos: Double) {
        IN(1.0), POWER(.86), HIGH(.86)
    }

    enum class Shoot(val pos: Double) {
        PRE(0.33), POST(.55)
    }

    private var command: Command = Command.idle

    fun shoot() {
        command = linear {
            task { feed(Shoot.POST) }
            delay(150)
            task { feed(Shoot.PRE) }
        }
    }

    fun burst() {
        command = linear {
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
        command = linear {
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
        command = linear {
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
        command.let {
            if (it.done)
                command = Command.idle
            else
                it.update(frame)
        }
    }
}