package com.brownicians.eventapp.viewmodels

import androidx.lifecycle.*
import com.brownicians.eventapp.*
import com.brownicians.eventapp.extensions.disposedBy
import com.brownicians.eventapp.models.EventModel
import com.brownicians.eventapp.repositories.EventRepository
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject

interface EventViewModel {
    class Factory(private val eventRepository: EventRepository, private val resultConverter: ResultConverter): ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel?> create(modelClass: Class<T>): T {
            return ViewModel(eventRepository, resultConverter) as T
        }
    }

    interface Inputs {
        val eventId: MutableLiveData<Int>

        val onCreate: MutableLiveData<Unit>
    }

    interface Outputs: ErrorEmissionCapable {
        val items: LiveData<List<EventItemModel>>
    }

    class ViewModel(private val eventRepository: EventRepository, private val resultConverter: ResultConverter): androidx.lifecycle.ViewModel(), Inputs, Outputs {
        val inputs: Inputs = this
        val outputs: Outputs = this
        override val disposeBag = DisposeBag()

        private val eventIdMediator: MediatorLiveData<Int>
        private val backingEventId: PublishSubject<Int> = PublishSubject.create()
        override val eventId = MutableLiveData<Int>()

        private val onCreateMediator: MediatorLiveData<Unit>
        private val backingOnCreate: PublishSubject<Unit> = PublishSubject.create()
        override val onCreate = MutableLiveData<Unit>()

        private val itemsMediator: MediatorLiveData<List<EventItemModel>>
        private val backingItems: PublishSubject<List<EventItemModel>> = PublishSubject.create()
        override val items = MutableLiveData<List<EventItemModel>>()

        override val error = MutableLiveData<OperationError>()

        private val eventResult: PublishSubject<OperationResult<EventModel?>> = PublishSubject.create()

        init {
            eventIdMediator = ObservableBinder<Int>().bind(inputs.eventId, backingEventId)
            onCreateMediator = ObservableBinder<Unit>().bind(inputs.onCreate, backingOnCreate)
            itemsMediator = ObservableBinder<List<EventItemModel>>().bind(outputs.items, backingItems)

            this.backingEventId
                .switchMap {
                    val operation = this.eventRepository.load(it)
                    this.resultConverter.convert(operation)
                }
                .subscribeBy(
                    onNext = { this.eventResult.onNext(it) }
                )
                .disposedBy(this.disposeBag)
            this.eventResult
                .filter { it.result != null }
                .map { it.result }
                .map {
                    this.createItems(it)
                }.subscribeBy(
                    onNext = { this.items.value = it }
                ).disposedBy(this.disposeBag)
            this.bindError(this.eventResult)
        }

        private fun createItems(eventModel: EventModel): List<EventItemModel> {
            val section1Title = EventItemModel("The basics", isHeading = true, showEditButton = true)
            val eventName = EventItemModel("Name: ${eventModel.name}", isHeading = false, showEditButton = false)
            val eventTime = EventItemModel("When: ${eventModel.date}", isHeading = false, showEditButton = false)
            val eventLocation = EventItemModel("Where: ${eventModel.location}", isHeading = false, showEditButton = false)
            return listOf(section1Title, eventName, eventTime, eventLocation)
        }
    }

    data class EventItemModel(val title: String, val isHeading: Boolean, val showEditButton: Boolean)
}