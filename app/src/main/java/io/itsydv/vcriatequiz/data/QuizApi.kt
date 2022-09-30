package io.itsydv.vcriatequiz.data

import io.itsydv.vcriatequiz.models.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface QuizApi {

    @GET("/")
    suspend fun getQuiz(@Query("quiz") quiz: Boolean = true): Response<ApiResponse>
}