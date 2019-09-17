package com.brownicians.eventapp
import com.brownicians.eventapp.extensions.disposedBy
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.subjects.PublishSubject

class WrapInErrorHandledResult<X>(private val errorMapper: ErrorMapper): ObservableTransformer<X, OperationResult<X>> {
    private val disposeBag = DisposeBag()
    private val wrappedResult: PublishSubject<OperationResult<X>> = PublishSubject.create()

    override fun apply(upstream: Observable<X>): ObservableSource<OperationResult<X>> {
        upstream.subscribe(
            {
                value ->
                wrappedResult.onNext(OperationResult(value, null))
            },
            { error ->
                val result: OperationResult<X> = errorMapper.handleAnyError(error)
                wrappedResult.onNext(result)
            }
        ).disposedBy(this.disposeBag)
        return wrappedResult
    }
}