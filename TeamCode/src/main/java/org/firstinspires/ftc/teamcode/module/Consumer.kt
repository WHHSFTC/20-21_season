package org.firstinspires.ftc.teamcode.module


@FunctionalInterface
interface NConsumer<T> {
    operator fun invoke(a: T): Unit
}

@FunctionalInterface
interface NSupplier<T> {
    operator fun invoke(): T
}

