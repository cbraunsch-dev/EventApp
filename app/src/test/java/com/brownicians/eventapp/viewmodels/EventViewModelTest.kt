package com.brownicians.eventapp.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.brownicians.eventapp.*
import com.brownicians.eventapp.models.EventModel
import com.brownicians.eventapp.repositories.EventRepository
import com.nhaarman.mockitokotlin2.anyOrNull
import io.reactivex.Observable
import org.junit.*
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.io.IOException

class EventViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var testee: EventViewModel.ViewModel

    @Mock
    private lateinit var mockEventRepository: EventRepository
    @Mock
    private lateinit var mockErrorMapper: ErrorMapper

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testee = EventViewModel.ViewModel(mockEventRepository, EventAppResultConverter(mockErrorMapper))
    }

    @After
    fun tearDown() {
    }

    @Test
    fun testOnCreate_then_emitInitialEventItems() {
        //Arrange
        val eventId = 1337
        val mockEvent = TestData.createMockEvent()
        `when`(this.mockEventRepository.load(ArgumentMatchers.eq(eventId))).thenReturn(Observable.just(mockEvent))
        testee.inputs.eventId.value = eventId

        //Act
        testee.inputs.onCreate.value = Unit

        //Assert
        val emittedItems = testee.outputs.items.value
        if (emittedItems?.size == 4) {
            Assert.assertEquals("The basics", emittedItems[0].title)
            Assert.assertTrue(emittedItems[0].isHeading)
            Assert.assertTrue(emittedItems[0].showEditButton)
            Assert.assertEquals("Name: ${mockEvent.name}", emittedItems[1].title)
            Assert.assertFalse(emittedItems[1].isHeading)
            Assert.assertFalse(emittedItems[1].showEditButton)
            Assert.assertEquals("When: ${mockEvent.date}", emittedItems[2].title)
            Assert.assertFalse(emittedItems[2].isHeading)
            Assert.assertFalse(emittedItems[2].showEditButton)
            Assert.assertEquals("Where: ${mockEvent.location}", emittedItems[3].title)
            Assert.assertFalse(emittedItems[3].isHeading)
            Assert.assertFalse(emittedItems[3].showEditButton)
        } else {
            Assert.fail("Failed to emit correct number of event items")
        }
    }

    @Test
    fun testOnCreate_when_failedToLoadEvent_then_emitError() {
        //Arrange
        val eventId = 1337
        val expectedOperationError = OperationError("An error occurred")
        val expectedOperationResult = OperationResult<EventModel?>(null, expectedOperationError)
        `when`(this.mockErrorMapper.handleAnyError<EventModel?>(anyOrNull())).thenReturn(expectedOperationResult)
        `when`(this.mockEventRepository.load(ArgumentMatchers.eq(eventId))).thenReturn(Observable.error(IOException()))
        testee.inputs.eventId.value = eventId

        //Act
        testee.inputs.onCreate.value = Unit

        //Assert
        val emittedError = testee.outputs.error.value
        Assert.assertNotNull(emittedError)
    }
}