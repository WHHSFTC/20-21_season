package org.firstinspires.ftc.teamcode.module

import com.qualcomm.robotcore.hardware.DcMotor
import kotlinx.coroutines.delay
import java.lang.Thread.sleep

class Indexer(val bot: Robot) {
    val feedServo = bot.hwmap.servo["feeder"]
    val heightServo = bot.hwmap.servo["setter"]

    enum class Height(val pos: Double) {
        IN(1.0), POWER(.87), HIGH(.86)
    }

    enum class Shoot(val pos: Double) {
        PRE(0.33), POST(.55)
    }

    fun shoot() {
        feed(Shoot.POST)
        sleep(150)
        feed(Shoot.PRE)
    }

    fun burst() {
        feed(Shoot.POST)
        sleep(150)
        feed(Shoot.PRE)
        sleep(150)

        feed(Shoot.POST)
        sleep(150)
        feed(Shoot.PRE)
        sleep(150)

        feed(Shoot.POST)
        sleep(150)
        feed(Shoot.PRE)
        sleep(150)

        feed(Shoot.POST)
        sleep(150)
        feed(Shoot.PRE)
        sleep(250)
        bot.out(Shooter.State.OFF)
        height(Height.IN)
    }

    fun shake() {
        height(Height.POWER)
        sleep(250)
        height(Height.IN)
        sleep(250)

        height(Height.POWER)
        sleep(250)
        height(Height.IN)
    }

    val feed = object : Module<Indexer.Shoot> {
        override var state: Shoot = Shoot.PRE
            set(value) {
                feedServo.position = value.pos
                field = value
            }
    }

    val height = object : Module<Indexer.Height> {
        override var state: Height = Height.IN
            set(value) {
                heightServo.position = value.pos
                field = value
            }
    }
}