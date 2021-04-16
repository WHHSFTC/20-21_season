package org.firstinspires.ftc.teamcode.switchboard.stores

class EndPoint<T>(initial: T, var sideEffect: (T) -> Unit): Observer<T> {
    var value: T = initial
        private set

    override fun next(x: T) {
        value = x
        sideEffect(x)
    }
}