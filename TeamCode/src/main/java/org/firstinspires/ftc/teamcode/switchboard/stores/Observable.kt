package org.firstinspires.ftc.teamcode.switchboard.stores

interface Observable<T> {
    fun subscribe(observer: Observer<T>): Subscription<T>
    fun subscribe(callback: (T) -> Unit): Subscription<T> = subscribe(Observer { callback(it) })

    fun unsubscribe(subscription: Subscription<T>)
}