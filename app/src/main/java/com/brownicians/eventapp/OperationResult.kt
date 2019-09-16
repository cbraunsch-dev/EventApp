package com.brownicians.eventapp

data class OperationResult<E>(val result: E?, val error: OperationError?)

data class OperationError(val userFriendlyMessage: String)