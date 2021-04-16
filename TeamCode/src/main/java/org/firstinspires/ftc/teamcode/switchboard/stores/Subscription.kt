package org.firstinspires.ftc.teamcode.switchboard.stores

data class Subscription<T>(val observable: Observable<T>, val observer: Observer<T>) {
    fun close() {
        observable.unsubscribe(this)
    }
}