package org.firstinspires.ftc.teamcode.switchboard.hardware

import org.firstinspires.ftc.teamcode.switchboard.stores.Observable

interface Encoder: HardwareInput {
    val position: Observable<Int>
    val velocity: Observable<Double>
}