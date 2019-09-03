package com.brownicians.eventapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.brownicians.eventapp.extensions.disposedBy
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject

class ObservableBinder<E: Any> {
    fun bind(liveData: LiveData<E>, subject: PublishSubject<E>): MediatorLiveData<E> {
        return MediatorLiveData<E>().apply {
            addSource(liveData) { value ->
                subject.onNext(value)
            }
        }.also { it.observeForever {} }
    }

    fun bind(observable: Observable<E>, liveData: MutableLiveData<E>, disposeBag: DisposeBag) {
        observable.subscribeBy(
            onNext = { liveData.value = it }
        ).disposedBy(disposeBag)
    }
}