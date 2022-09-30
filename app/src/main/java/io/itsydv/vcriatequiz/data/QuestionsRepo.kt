package io.itsydv.vcriatequiz.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.itsydv.vcriatequiz.main.Resource
import io.itsydv.vcriatequiz.models.ApiResponse
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class QuestionsRepo {

    suspend fun getQuestions(): LiveData<Resource<Response<ApiResponse>>> {
        val questions = MutableLiveData<Resource<Response<ApiResponse>>>()
        questions.postValue(Resource.Loading())
        try {
            val response = RetrofitInstance.api.getQuiz(true)
            questions.postValue(Resource.Success(response))
        } catch (e: IOException) {
            questions.postValue(Resource.Error("Connection Error"))
        } catch (e: HttpException) {
            questions.postValue(Resource.Error("Something went wrong"))
        } catch (e: Exception) {
            questions.postValue(Resource.Error(e.message.toString()))
        }
        return questions
    }
}