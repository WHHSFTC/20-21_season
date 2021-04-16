package org.firstinspires.ftc.teamcode.switchboard.stores

class StartPoint<T>(initial: T): PushObservable<T>() {
    var value: T = initial
        set(value) {
            update(value)
            field = value
        }
}