package com.brownicians.eventapp.repositories

import io.reactivex.Observable
import com.brownicians.eventapp.models.EventModel

interface EventRepository {
    fun save(name: String, date: String, location: String, password: String?): Observable<EventModel>

    fun load(eventId: Int): Observable<EventModel?>
}

class InMemoryEventRepository: EventRepository {
    override fun save(name: String, date: String, location: String, password: String?): Observable<EventModel> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun load(eventId: Int): Observable<EventModel?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}