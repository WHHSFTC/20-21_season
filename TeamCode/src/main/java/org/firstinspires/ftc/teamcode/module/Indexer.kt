package org.firstinspires.ftc.teamcode.module

import com.qualcomm.robotcore.hardware.Servo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Indexer(val bot: Robot) {
    val feedServo: Servo = bot.hwmap.servo["feeder"]
    val heightServo: Servo = bot.hwmap.servo["setter"]

    enum class Height(val pos: Double) {
        IN(1.0), POWER(.87), HIGH(.86)
    }

    enum class Shoot(val pos: Double) {
        PRE(0.33), POST(.55)
    }

    suspend fun shoot() {
        feed(Shoot.POST)
        delay(150)
        feed(Shoot.PRE)
    }

    suspend fun burst() {
        repeat(3) {
            feed(Shoot.POST)
            delay(150)
            feed(Shoot.PRE)
            delay(150)
        }

        feed(Shoot.POST)
        delay(150)
        feed(Shoot.PRE)
        delay(250)
        
        bot.out(Shooter.State.OFF)
        height(Height.IN)
    }

    suspend fun slowBurst() {
        repeat(3) {
            feed(Shoot.POST)
            delay(150)
            feed(Shoot.PRE)
            delay(1000)
        }

        feed(Shoot.POST)
        delay(150)
        feed(Shoot.PRE)
        delay(250)

        bot.out(Shooter.State.OFF)
        height(Height.IN)
    }
    suspend fun shake() {
        GlobalScope.launch {
            height(Height.POWER)
            delay(250)
            height(Height.IN)
            delay(250)

            height(Height.POWER)
            delay(250)
            height(Height.IN)
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
}