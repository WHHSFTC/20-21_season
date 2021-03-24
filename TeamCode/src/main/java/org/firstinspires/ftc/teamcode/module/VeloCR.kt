package org.firstinspires.ftc.teamcode.module

import com.qualcomm.robotcore.hardware.CRServo

class VeloCR(val cr: CRServo): Module<Double> {
    override var state: Double = 0.0
        set(value) {
            cr.power = value
            field = value
        }
}