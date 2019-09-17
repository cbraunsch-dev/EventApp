package com.brownicians.eventapp.models

data class EventModel(val id: Int, val name: String, val description: String, val date: String, val location: String, val password: String?, val participants: List<ParticipantModel>, val tasks: List<TaskModel>)