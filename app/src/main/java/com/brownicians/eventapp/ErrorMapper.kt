package com.brownicians.eventapp

interface ErrorMapper {
    fun <T> handleAnyError(error: Throwable): OperationResult<T>
}

class LocalizedStringErrorMapper: ErrorMapper {
    override fun <T> handleAnyError(error: Throwable): OperationResult<T> {
        return OperationResult(null, OperationError("An error has occurred"))
    }
}