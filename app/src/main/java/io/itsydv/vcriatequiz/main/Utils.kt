package io.itsydv.vcriatequiz.main

import java.math.BigInteger
import java.security.MessageDigest

// Resource class to store the current state of our request
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}

object Gravatar {
    fun getProfileUrl(userId: String): String? {
        var hex = ""
        try {
            val digest = MessageDigest.getInstance("MD5")
            val hash: ByteArray = digest.digest(userId.toByteArray())
            val bigInt = BigInteger(hash)
            hex = bigInt.abs().toString(16)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return "https://www.gravatar.com/avatar/$hex?d=identicon"
    }
}