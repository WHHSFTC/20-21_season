package org.firstinspires.ftc.teamcode.module

import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.ServoImplEx

class StatefulServo<T: StatefulServo.ServoPosition>(val servo: Servo, initial: T): Module<T> {
//    constructor(servo: Servo, initial: T): this(servo as ServoImplEx, initial)
    override var state: T = initial
        set(value) {
//            when {
//                value.en && !servo.isPwmEnabled -> {
//                    servo.setPwmEnable()
//                }
//                !value.en && servo.isPwmEnabled -> {
//                    servo.setPwmDisable()
//                }
//            }
            servo.position = value.pos
            field = value
        }

    interface ServoPosition {
        val pos: Double
//        val en: Boolean
    }
}