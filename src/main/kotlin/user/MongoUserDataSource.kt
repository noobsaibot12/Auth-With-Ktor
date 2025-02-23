package com.example.user

import com.mongodb.client.MongoDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection

class MongoUserDataSource(

    private val database: MongoDatabase

): UserDataSource {

    private val usersCollection = database.getCollection<User>()

    override suspend fun getUserByEmail(email: String): User? {

        return usersCollection.findOne( User::email eq email )

    }

    override suspend fun insertUser(user: User): Boolean {

        val sameUser = usersCollection.findOne( User::email eq user.email )

        return if ( sameUser == null ) {

            usersCollection.insertOne( user ).wasAcknowledged()

        } else {

            false

        }

    }


}