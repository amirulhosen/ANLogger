package com.logger

/**
 * A use case that requires no input parameters, yet returns a value.
 *
 * @param R outputs
 */
interface UseCase<R> : Cancellable {
    fun execute(): R
}

/**
 * A use case that requires no input parameters and does not return a value. Suitable for fire and forget
 * operations.
 *
 */
interface CompletableUseCase : Cancellable {
    fun execute()
}

/**
 * A use case that requires input parameters and returns a value.
 *
 * @param P inputs
 * @param R outputs
 */
interface UseCaseWithParameter<P, R> :
    Cancellable {
    fun execute(parameter: P): R
}

/**
 * A use case that requires input parameters and does not return a value. Suitable for fire and forget
 * operations.
 *
 * @param P inputs
 */
interface CompletableUseCaseWithParameter<P> :
    Cancellable {
    fun execute(parameter: P)
}

/**
 * All use cases conform to the [Cancellable] interface. The cancel method informs the [UseCase] that
 * the caller does not care about the result anymore. This is a good place to cancel and cleanup the
 * operation.
 *
 */
interface Cancellable {
    fun cancel()
}
