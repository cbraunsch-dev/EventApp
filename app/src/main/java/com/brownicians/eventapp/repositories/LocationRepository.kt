package com.brownicians.eventapp.repositories

import com.brownicians.eventapp.models.LocationModel
import io.reactivex.Observable

interface LocationRepository {
    fun obtainLocation(): Observable<LocationModel>
}

class InMemoryLocationRepository: LocationRepository {
    override fun obtainLocation(): Observable<LocationModel> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}