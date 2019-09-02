package com.brownicians.eventapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import com.brownicians.eventapp.R
import com.brownicians.eventapp.databinding.ActivityCreateEventBinding
import datastores.InMemoryEventDataStore
import viewmodels.CreateEventViewModel

class CreateEventActivity : AppCompatActivity() {
    private var viewModel: CreateEventViewModel.ViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityCreateEventBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_event)
        this.viewModel = ViewModelProviders.of(this, CreateEventViewModel.Factory(InMemoryEventDataStore()))[CreateEventViewModel.ViewModel::class.java]
        binding.viewmodel = this.viewModel
        binding.lifecycleOwner = this
    }
}
