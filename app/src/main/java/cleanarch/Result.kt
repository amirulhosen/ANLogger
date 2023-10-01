package cleanarch

/**
 * Encapsulates the status of an operation with either [Success.data] of T, [Error.exception] or [Loading]
 * to indicate that the operation is still executing. Result is typically used for wrapping the output
 * of a Use Case.
 *
 * Not to be confused with [Response] which is intended for repository actions.
 *
 * @param T
 */
sealed class Result<out T> {

    /**
     * Encapsulates the successful completion of an operation.
     *
     * @param T
     * @property data The result of the operation.
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * Encapsulates the failure of an operation.
     *
     * @property exception The exception produced by the operation
     */
    data class Error(val exception: Exception) : Result<Nothing>()

    /**
     * Encapsulates the state that indicates the operation is currently executing.
     */
    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }
}

val Result<*>.succeeded
    get() = this is Result.Success && data != null
