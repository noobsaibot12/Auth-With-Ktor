package com.example.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(

    val token: String

)
