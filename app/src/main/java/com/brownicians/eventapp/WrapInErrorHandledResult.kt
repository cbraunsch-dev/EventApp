package com.brownicians.eventapp
import com.brownicians.eventapp.extensions.disposedBy
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject

class WrapInErrorHandledResult<X: Any>(private val errorMapper: ErrorMapper): ObservableTransformer<X, OperationResult<X>> {
    private val disposeBag = DisposeBag()
    private val wrappedResult: PublishSubject<OperationResult<X>> = PublishSubject.create()

    override fun apply(upstream: Observable<X>): ObservableSource<OperationResult<X>> {
        upstream.subscribeBy (
            onNext = {
                wrappedResult.onNext(OperationResult(it, null))
            },
            onError = {
                val result: OperationResult<X> = errorMapper.handleAnyError(it)
                wrappedResult.onNext(result)
            }
        ).disposedBy(this.disposeBag)
        return wrappedResult
    }
}