package com.brownicians.eventapp.extensions

import com.brownicians.eventapp.DisposeBag
import io.reactivex.disposables.Disposable

fun Disposable.disposedBy(bag: DisposeBag) {
    bag.addDisposable(this)
}