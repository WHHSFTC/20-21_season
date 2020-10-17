package org.firstinspires.ftc.teamcode.module

import com.qualcomm.robotcore.hardware.Servo

class StatefulServo<T: StatefulServo.ServoPosition>(val servo: Servo, initial: T): Module<T> {
    override var state: T = initial
        set(value) {
            servo.position = value.pos
            field = value
        }

    interface ServoPosition {
        val pos: Double
    }
}