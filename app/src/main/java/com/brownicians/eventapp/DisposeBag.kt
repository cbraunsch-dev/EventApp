package com.brownicians.eventapp

import io.reactivex.disposables.Disposable

class DisposeBag {
    private val disposables = ArrayList<Disposable>()

    fun addDisposable(disposable: Disposable) {
        this.disposables.add(disposable)
    }

    fun clearBag() {
        disposables.forEach {
            it.dispose()
        }
        disposables.clear()
    }
}