package com.example.konsulsehat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _loggedInUser = MutableLiveData<String>()
    val loggedInUser: LiveData<String> get() = _loggedInUser

    fun setLoggedInUser(user: String) {
        _loggedInUser.value = user
    }
}