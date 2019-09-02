package datastores

import io.reactivex.Observable
import models.EventModel

interface EventDataStore {
    fun save(name: String, date: String, location: String, password: String?): Observable<EventModel>

    fun load(eventId: Int): Observable<EventModel?>
}

class InMemoryEventDataStore: EventDataStore {
    override fun save(name: String, date: String, location: String, password: String?): Observable<EventModel> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun load(eventId: Int): Observable<EventModel?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}