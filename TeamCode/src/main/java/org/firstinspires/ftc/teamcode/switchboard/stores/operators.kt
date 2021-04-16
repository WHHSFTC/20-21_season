package org.firstinspires.ftc.teamcode.switchboard.stores

import org.firstinspires.ftc.teamcode.switchboard.core.Logger

fun <P, R> Observable<P>.map(f: (P) -> R): Observable<R>
    = LazySubject<P, R>(f).also { this.subscribe(it) }

fun <P, R> Observable<P>.scan(initial: R, f: (acc: R, P) -> R): Observable<R>
    = RollingSubject<P, R>(initial, f).also { this.subscribe(it) }

fun <P> Observable<P>.filter(predicate: (P) -> Boolean): Observable<P>
    = FilterSubject<P>(predicate).also { this.subscribe(it) }

fun <T> Observable<Observable<T>>.flatten(): Observable<T>
    = SwappableSubject<T>().also { this.subscribe(it) }

fun <T> Observable<T>.log(logStream: Logger.LogStream, name: String)
    = this.subscribe { logStream[name] = it }

fun <T: Observable<*>> T.tap(block: T.() -> Unit)
    = this.apply(block)

fun <T> Observable<T>.inject(): Subject<T, T>
    = IdentitySubject<T>().also { this.subscribe(it) }

fun Observable<Boolean>.debounce(): Observable<Unit>
    = this.scan(false to false) { acc, n -> n to acc.first }.filter { it.first && !it.second }.map { Unit }

infix fun <T> Observable<T>.bind(observer: Observer<T>): Subscription<T>
    = this.subscribe(observer)

// localizer.pose bind drivetrain.pose
