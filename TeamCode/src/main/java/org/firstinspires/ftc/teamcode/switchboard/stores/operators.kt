package org.firstinspires.ftc.teamcode.switchboard.stores

import org.firstinspires.ftc.teamcode.switchboard.core.Logger

fun <P, R> Observable<P>.map(f: (P) -> R): Observable<R>
    = LazySubject<P, R>(f).also { this bind it }

fun <P, R> Observable<P>.scan(initial: R, f: (acc: R, P) -> R): Observable<R>
    = RollingSubject<P, R>(initial, f).also { this bind it }

fun <T> Observable<T>.filter(predicate: (T) -> Boolean): Observable<T>
    = FilterSubject<T>(predicate).also { this bind it }

fun <P, R> Observable<P>.edgeScan(initial: (P) -> R, f: (acc: R, P) -> R): Observable<R>
    = this.scan<P, R?>(null) { acc, it -> if (acc == null) initial(it) else f(acc, it) }.map { it!! }

fun <T> Observable<T>.pairwise(): Observable<Pair<T, T>>
    = this.scan<T, Pair<T?, T?>>(null to null) { (a, _), new -> new to a }.filter { it.first != null && it.second != null }.map { it.first!! to it.second!! }

fun <T> Observable<T>.dedup(initial: T): Observable<T>
    = this.scan(initial to initial) { (a, _), new -> new to a }.filter { it.first != it.second }.map { it.first }

fun <T> Observable<Observable<T>>.flatten(): Observable<T>
    = SwappableSubject<T>().also { this bind it }

fun <T> Observable<T>.log(loggerStream: Logger.LogStream, name: String)
    = this.subscribe { loggerStream[name] = it }

fun <T: Observable<*>> T.tap(block: T.() -> Unit)
    = this.apply(block)

fun <T> Observable<T>.taplog(loggerStream: Logger.LogStream, name: String): Observable<T>
        = this.tap { log(loggerStream, name) }

fun <T> Observable<T>.inject(): Subject<T, T>
    = SimpleSubject<T>().also { this bind it }

fun Observable<Boolean>.posEdge(): Observable<Unit>
    = this.scan(false to false) { acc, n -> n to acc.first }.filter { it.first && !it.second }.map { Unit }

infix fun <T> Observable<T>.bind(observer: Observer<T>): Subscription<T>
    = this.subscribe(observer)

infix fun <T> Observable<T>.bind(f: (T) -> Unit): Subscription<T>
        = this.subscribe(f)

fun <T> Observable<T>.comment(s: String)
    = this

fun <T> Observable<T>.store(initial: T): Store<T>
    = Store<T>(initial).also { this bind it }

fun <T> entry(): Subject<T, T>
    = SimpleSubject<T>()

fun <T, S : T> Observable<S>.upCast(): Observable<T>
    = this.map { it }

fun <T, A> Observable<T>.turnstile(arbiter: Observable<A>): Observable<T>
    = TurnstileSubject<T, A>().also { this bind it; arbiter bind it.pass }

// .comment("localizer.pose bind drivetrain.pose")
