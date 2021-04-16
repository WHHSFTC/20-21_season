package org.firstinspires.ftc.teamcode.switchboard.stores

abstract class PushObservable<R>: Observable<R> {
    protected val subscriptions: MutableSet<Subscription<R>> = mutableSetOf()

    override fun subscribe(observer: Observer<R>): Subscription<R>
            = Subscription(this, observer).also { subscriptions += it }

    override fun unsubscribe(subscription: Subscription<R>) {
        subscriptions -= subscription
    }

    protected fun update(y: R) {
        subscriptions.forEach { it.observer.next(y) }
    }
}