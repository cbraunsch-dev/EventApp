package com.brownicians.eventapp.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.brownicians.eventapp.ErrorMapper
import com.brownicians.eventapp.TestData
import com.brownicians.eventapp.repositories.EventRepository
import io.reactivex.Observable
import org.junit.*
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.eq
import org.mockito.MockitoAnnotations

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
        testee = EventViewModel.ViewModel(mockEventRepository, mockErrorMapper)
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
}