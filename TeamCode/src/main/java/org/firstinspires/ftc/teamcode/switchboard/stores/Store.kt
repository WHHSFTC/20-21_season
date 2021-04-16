package org.firstinspires.ftc.teamcode.switchboard.stores

class Store<T>(initial: T): Subject<T, T>() {
    var value: T = initial
        private set

    override fun next(x: T) {
        value = x
        update(x)
    }
}