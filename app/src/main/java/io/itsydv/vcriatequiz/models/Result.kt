package io.itsydv.vcriatequiz.models

import androidx.annotation.Keep

@Keep
data class Result(
    val questions: List<Question>,
    val timeInMinutes: Int
)