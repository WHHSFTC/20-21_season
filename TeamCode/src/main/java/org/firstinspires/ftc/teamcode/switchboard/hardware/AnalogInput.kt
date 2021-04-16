package org.firstinspires.ftc.teamcode.switchboard.hardware

import org.firstinspires.ftc.teamcode.switchboard.stores.Observable

interface AnalogInput: HardwareInput {
    val voltage: Observable<Double>
    val maxVoltage: Observable<Double>
}