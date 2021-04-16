package org.firstinspires.ftc.teamcode.switchboard.hardware

import org.firstinspires.ftc.teamcode.switchboard.stores.Observable

interface DigitalInput: HardwareInput {
    val high: Observable<Boolean>
}