package org.firstinspires.ftc.teamcode.module

import com.qualcomm.robotcore.hardware.CRServo

class Stateful<B, W: Stateful.Wrap<B>>(val module: Module<B>, initial: W): Module<W> {
    override var state: W = initial
        set(value) {
            module(value.v)
            field = value
        }

    interface Wrap<B> {
        val v: B
    }
}