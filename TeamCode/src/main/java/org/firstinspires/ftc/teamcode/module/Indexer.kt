package org.firstinspires.ftc.teamcode.module

import com.qualcomm.robotcore.hardware.DcMotor

class Indexer(bot: Robot) {
    val feedServo = bot.hwmap.servo["indexer"]
    val heightServo = bot.hwmap.servo["indexerHeight"]

    enum class Height(val pos: Double) {
        IN(.96), OUT(.8)
    }

    enum class Shoot(val pos: Double) {
        PRE(0.0), ONE(.33), TWO(.67), THREE(1.0)
    }

    fun shootStep() {
        if (height.state == Height.IN) {
            height.state = Height.OUT
        } else {
            prime.state = when (prime.state) {
                Shoot.PRE -> Shoot.ONE
                Shoot.ONE -> Shoot.TWO
                Shoot.TWO -> Shoot.THREE
                Shoot.THREE -> Shoot.PRE
            }
        }
    }

    val prime = object : Module<Indexer.Shoot> {
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