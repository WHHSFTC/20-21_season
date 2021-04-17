package org.firstinspires.ftc.teamcode.switchboard.stores

class PullObservable<T>(val f: () -> T) : Observable<T> {
    override fun subscribe(observer: Observer<T>): Subscription<T> {
        observer.next(f())
        return Subscription(this, observer)
    }

    override fun unsubscribe(subscription: Subscription<T>) {

    }
}