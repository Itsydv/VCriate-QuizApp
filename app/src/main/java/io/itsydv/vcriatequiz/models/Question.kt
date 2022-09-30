package io.itsydv.vcriatequiz.models

import androidx.annotation.Keep

@Keep
data class Question(
    val correct_answers: List<Int>,
    val lable: String,
    val options: List<Option>
)