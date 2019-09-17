package com.brownicians.eventapp

import com.brownicians.eventapp.models.EventModel

class TestData {
    companion object Generator {
        fun createMockEvent(): EventModel {
            return EventModel(1, "Birthday bash", "Josh's 30th birthday party, whoohoo!", "01.10.2019", "Josh's place", null, listOf(), listOf())
        }

        //TODO: createMockEventWithTasks
        //TODO: createMockEventWithTasksAndParticipants
    }
}