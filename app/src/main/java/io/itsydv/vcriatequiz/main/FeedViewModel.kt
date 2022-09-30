package io.itsydv.vcriatequiz.main

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.itsydv.vcriatequiz.data.QuestionsRepo
import io.itsydv.vcriatequiz.models.ApiResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class FeedViewModel: ViewModel() {

    private val repo = QuestionsRepo()
    private val _questions = MediatorLiveData<Resource<Response<ApiResponse>>>()
    val questions get() = _questions

    init {
        getQuestions()
    }

    private fun getQuestions() = viewModelScope.launch {
        _questions.postValue(Resource.Loading())
        _questions.addSource(repo.getQuestions()) {
            _questions.postValue(it)
        }
    }
}