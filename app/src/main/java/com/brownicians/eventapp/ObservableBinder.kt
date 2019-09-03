package com.brownicians.eventapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import io.reactivex.subjects.PublishSubject

class ObservableBinder<E> {
    fun bind(liveData: LiveData<E>, subject: PublishSubject<E>): MediatorLiveData<E> {
        return MediatorLiveData<E>().apply {
            addSource(liveData) { value ->
                subject.onNext(value)
            }
        }.also { it.observeForever {} }
    }
}