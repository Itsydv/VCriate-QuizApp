package io.itsydv.vcriatequiz.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QuestionViewModel: ViewModel() {

    var questionNumber = MutableLiveData(0)
}