package com.brownicians.eventapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import datastores.EventDataStore
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import junit.framework.Assert.assertEquals
import models.EventModel
import models.ParticipantModel
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import viewmodels.CreateEventViewModel

class CreateEventViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var testee: CreateEventViewModel.ViewModel

    @Mock
    private lateinit var mockEventDataStore: EventDataStore

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testee = CreateEventViewModel.ViewModel(mockEventDataStore)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun testEventName_when_eventDateAndEventLocationNotEmpty_then_createButtonEnabled() {
        //Arrange
        testee.inputs.eventDate.value = "10.01.2019"
        testee.inputs.location.value = "Munich"

        //Act
        testee.inputs.eventName.value = "John Doe"

        //Assert
        assertEquals(true, testee.outputs.createButtonEnabled.value)
    }

    @Test
    fun testCreateButtonTaps_then_saveEvent() {
        //Arrange
        val eventName = "Birthday party"
        testee.inputs.eventName.value = eventName
        testee.inputs.eventDate.value = "10.01.2019"
        testee.inputs.location.value = "Munich"
        testee.inputs.password.value = "12345"
        val createdEvent = EventModel(0, "", "", "", "", "", listOf())
        `when`(this.mockEventDataStore.save(any(), any(), any(), any())).thenReturn(Observable.just(createdEvent))

        //Act
        testee.inputs.createButtonTaps.value = Unit

        //Assert
        Mockito.verify(this.mockEventDataStore, times(1)).save(eq(eventName), any(), any(), any())
    }

    @Test
    fun testCreateButtonTaps_when_savedEvent_then_emitEventId() {
        //Arrange
        val eventId = 1337
        val createdEvent = EventModel(eventId, "", "", "", "", "", listOf())
        testee.inputs.eventName.value = "Birthday"
        testee.inputs.eventDate.value = "10.01.2019"
        testee.inputs.location.value = "Munich"
        testee.inputs.password.value = "12345"
        `when`(this.mockEventDataStore.save(any(), any(), any(), any())).thenReturn(Observable.just(createdEvent))

        //Act
        testee.inputs.createButtonTaps.value = Unit

        //Assert
        assertEquals(eventId, testee.outputs.eventId.value)
    }
}