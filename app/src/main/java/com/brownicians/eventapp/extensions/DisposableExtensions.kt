package com.brownicians.eventapp.extensions

import com.brownicians.eventapp.viewmodels.CreateEventViewModel
import io.reactivex.disposables.Disposable

fun Disposable.disposedBy(bag: CreateEventViewModel.DisposeBag) {
    bag.addDisposable(this)
}