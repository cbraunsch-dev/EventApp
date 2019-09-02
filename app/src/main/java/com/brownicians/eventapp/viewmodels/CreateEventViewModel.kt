package com.brownicians.eventapp.viewmodels

import android.util.Log
import androidx.lifecycle.*
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

        private val createButtonEnabledDisposable: Disposable
        private val backingCreateButtonEnabled: PublishSubject<Boolean> = PublishSubject.create()
        override val createButtonEnabled = MutableLiveData<Boolean>()

        override val eventId = MutableLiveData<Int>()

        private val userInputValidDisposable: Disposable
        private val userInputDisposable: Disposable
        private val saveEventDisposable: Disposable
        private val saveEventResultDisposable: Disposable

        private val userInput: PublishSubject<UserInput> = PublishSubject.create()
        private val saveEventResult: PublishSubject<EventModel> = PublishSubject.create()

        init {
            eventNameMediator = MediatorLiveData<String>().apply {
                addSource(inputs.eventName) { value ->
                    backingEventName.onNext(value)
                }
            }.also { it.observeForever {} }
            eventDateMediator = MediatorLiveData<String>().apply {
                addSource(inputs.eventDate) { value ->
                    backingEventDate.onNext(value)
                }
            }.also { it.observeForever {} }
            locationMediator = MediatorLiveData<String>().apply {
                addSource(inputs.location) { value ->
                    backingLocation.onNext(value)
                }
            }.also { it.observeForever {} }
            passwordMediator = MediatorLiveData<String>().apply {
                addSource(inputs.password) { value ->
                    backingPassword.onNext(value)
                }
            }.also { it.observeForever {} }
            createButtonTapsMediator = MediatorLiveData<Unit>().apply {
                addSource(inputs.createButtonTaps) { value ->
                    backingCreateButtonTaps.onNext(value)
                }
            }.also { it.observeForever {} }

            this.createButtonEnabledDisposable = this.backingCreateButtonEnabled
                .subscribeBy(
                    onNext = { this.createButtonEnabled.value = it }
                )

            val userInputValid = Observable.combineLatest<String, String, String, Boolean>(this.backingEventName, this.backingEventDate, this.backingLocation, Function3 { event, date, location ->
                Log.e("CreateEventViewModel", "Event name: $event")
                Log.e("CreateEventViewModel", "Date:  $date")
                Log.e("CreateEventViewModel", "Location : $location")
                event.isNotEmpty() && date.isNotEmpty() && location.isNotEmpty()
            })

            userInputValidDisposable = userInputValid.subscribeBy(
                onNext = { this.createButtonEnabled.value = it }
            )

            userInputDisposable = backingCreateButtonTaps
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
                )
            this.saveEventDisposable = this.userInput
                .switchMap {
                    this.eventRepository.save(it.name, it.date, it.location, it.password)
                }.subscribeBy(
                    onNext = { this.saveEventResult.onNext(it) }
                )
            this.saveEventResultDisposable = this.saveEventResult
                .subscribeBy(
                    onNext = { this.eventId.value = it.id }
                )
        }

        override fun onCleared() {
            userInputValidDisposable.dispose()
            saveEventResultDisposable.dispose()
            saveEventDisposable.dispose()
            createButtonEnabledDisposable.dispose()
            userInputDisposable.dispose()
        }

        data class UserInput(val name: String, val location: String, val date: String, val password: String)
    }
}
