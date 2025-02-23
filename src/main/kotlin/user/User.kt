package com.example.user

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class User(

    @BsonId val id: String = ObjectId().toString(),
    val email: String,
    val password: String, // Store as hashed password
    val salt: String

)