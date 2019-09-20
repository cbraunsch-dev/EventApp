package com.brownicians.eventapp

import com.brownicians.eventapp.extensions.disposedBy
import io.reactivex.Observable

interface ResultConverter {
    fun <E> convert(result: Observable<E>): Observable<OperationResult<E>>
}

class EventAppResultConverter(private val errorMapper: ErrorMapper): ResultConverter {
    private val disposeBag = DisposeBag()

    override fun <E> convert(result: Observable<E>): Observable<OperationResult<E>> {
        return Observable.create { emitter ->
            result.subscribe(
                {
                    value ->
                    emitter.onNext(OperationResult(value, null))
                    emitter.onComplete()
                },
                { error ->
                    val operationResult: OperationResult<E> = errorMapper.handleAnyError(error)
                    emitter.onNext(operationResult)
                    emitter.onComplete()
                }
            ).disposedBy(disposeBag)
        }
    }

}