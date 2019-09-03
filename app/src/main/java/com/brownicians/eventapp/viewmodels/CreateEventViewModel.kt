package com.brownicians.eventapp.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.brownicians.eventapp.DisposeBag
import com.brownicians.eventapp.ObservableBinder
import com.brownicians.eventapp.extensions.disposedBy
import com.brownicians.eventapp.repositories.EventRepository
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function3
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.PublishSubject
import com.brownicians.eventapp.models.EventModel

interface CreateEventViewModel {
    class Factory(private val eventRepository: EventRepository): ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel?> create(modelClass: Class<T>): T {
            return ViewModel(eventRepository) as T
        }
    }

    interface Inputs {
        val eventName: MutableLiveData<String>

        val eventDate: MutableLiveData<String>

        val location: MutableLiveData<String>

        val password: MutableLiveData<String>

        val createButtonTaps: MutableLiveData<Unit>
    }

    interface Outputs {
        val createButtonEnabled: LiveData<Boolean>

        val eventId: LiveData<Int>
    }

    class ViewModel(private val eventRepository: EventRepository): androidx.lifecycle.ViewModel(),
        Outputs,
        Inputs {
        val inputs: Inputs = this
        val outputs: Outputs = this
        private val disposeBag = DisposeBag()

        private val eventNameMediator: MediatorLiveData<String>
        private val backingEventName: PublishSubject<String> = PublishSubject.create()
        override val eventName = MutableLiveData<String>()

        private val eventDateMediator: MediatorLiveData<String>
        private val backingEventDate: PublishSubject<String> = PublishSubject.create()
        override val eventDate = MutableLiveData<String>()

        private val locationMediator: MediatorLiveData<String>
        private val backingLocation: PublishSubject<String> = PublishSubject.create()
        override val location = MutableLiveData<String>()

        private val passwordMediator: MediatorLiveData<String>
        private val backingPassword: PublishSubject<String> = PublishSubject.create()
        override val password = MutableLiveData<String>()

        private val createButtonTapsMediator: MediatorLiveData<Unit>
        private val backingCreateButtonTaps: PublishSubject<Unit> = PublishSubject.create()
        override val createButtonTaps = MutableLiveData<Unit>()

        private val backingCreateButtonEnabled: PublishSubject<Boolean> = PublishSubject.create()
        override val createButtonEnabled = MutableLiveData<Boolean>()

        override val eventId = MutableLiveData<Int>()

        private val userInput: PublishSubject<UserInput> = PublishSubject.create()
        private val saveEventResult: PublishSubject<EventModel> = PublishSubject.create()

        init {
            eventNameMediator = ObservableBinder<String>().bind(inputs.eventName, backingEventName)
            eventDateMediator = ObservableBinder<String>().bind(inputs.eventDate, backingEventDate)
            locationMediator = ObservableBinder<String>().bind(inputs.location, backingLocation)
            passwordMediator = ObservableBinder<String>().bind(inputs.password, backingPassword)
            createButtonTapsMediator = ObservableBinder<Unit>().bind(inputs.createButtonTaps, backingCreateButtonTaps)

            this.backingCreateButtonEnabled
                .subscribeBy(
                    onNext = { this.createButtonEnabled.value = it }
                ).disposedBy(this.disposeBag)


            val userInputValid = Observable.combineLatest<String, String, String, Boolean>(this.backingEventName, this.backingEventDate, this.backingLocation, Function3 { event, date, location ->
                Log.e("CreateEventViewModel", "Event name: $event")
                Log.e("CreateEventViewModel", "Date:  $date")
                Log.e("CreateEventViewModel", "Location : $location")
                event.isNotEmpty() && date.isNotEmpty() && location.isNotEmpty()
            })

            ObservableBinder<Boolean>().bind(userInputValid, this.createButtonEnabled, this.disposeBag)

            backingCreateButtonTaps
                .withLatestFrom<Unit, String, String, String, String, UserInput>(backingEventName, backingLocation, backingEventDate, backingPassword, object: Function5<Unit, String, String, String, String, UserInput> {
                    override fun invoke(t1: Unit, t2: String, t3: String, t4: String, t5: String): UserInput {
                        return UserInput(
                            t2,
                            t3,
                            t4,
                            t5
                        )
                    }
                }).subscribeBy (
                    onNext = { this.userInput.onNext(it) }
                ).disposedBy(this.disposeBag)
            this.userInput
                .switchMap {
                    this.eventRepository.save(it.name, it.date, it.location, it.password)
                }.subscribeBy(
                    onNext = { this.saveEventResult.onNext(it) }
                ).disposedBy(this.disposeBag)
            this.saveEventResult
                .subscribeBy(
                    onNext = { this.eventId.value = it.id }
                ).disposedBy(this.disposeBag)
        }

        override fun onCleared() {
            this.disposeBag.clearBag()
        }

        data class UserInput(val name: String, val location: String, val date: String, val password: String)
    }
}

