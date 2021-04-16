package org.firstinspires.ftc.teamcode.switchboard.stores

abstract class Subject<P, R>: Observer<P>, PushObservable<R>()

class LazySubject<P, R>(private val transform: (P) -> R): Subject<P, R>() {
    override fun next(x: P) {
        val y = transform(x)
        update(y)
    }
}

class RollingSubject<P, R>(private var y: R, private val transform: (R, P) -> R): Subject<P, R>() {
    override fun next(x: P) {
        y = transform(y, x)
        update(y)
    }
}

class FilterSubject<P>(private val predicate: (P) -> Boolean): Subject<P, P>() {
    override fun next(x: P) {
        if (predicate(x))
            update(x)
    }
}

class SimpleSubject<P>: Subject<P, P>() {
    override fun next(x: P) {
        update(x)
    }
}

class SwappableSubject<R>: Subject<Observable<R>, R>() {
    private var subscription: Subscription<R>? = null

    private val observer = Observer<R> {
        update(it)
    }

    override fun next(x: Observable<R>) {
        subscription?.close()
        subscription = x bind observer
    }
}
