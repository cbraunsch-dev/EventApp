package com.brownicians.eventapp

import androidx.lifecycle.MutableLiveData
import com.brownicians.eventapp.extensions.disposedBy
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy

interface ErrorEmissionCapable {
    val error: MutableLiveData<OperationError>
    val disposeBag: DisposeBag

    fun <E> bindError(result: Observable<OperationResult<E>>) {
        result.subscribeBy (
            onNext = {
                when {
                    it.error != null -> error.value = it.error
                }
            }
        ).disposedBy(this.disposeBag)
    }
}