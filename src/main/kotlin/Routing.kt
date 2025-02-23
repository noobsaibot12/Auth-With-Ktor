package com.example

import com.example.security.hashing.HashingService
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import com.example.user.User
import com.example.user.UserDataSource
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable


@Serializable
data class ResponseData(

    val title: String,
    val description: String

)


fun Application.configureRouting(

    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig

) {

    routing {

        get("/") {

            val a = mutableListOf(
                ResponseData("Go To Swimming", "Get In To The Pool And Swim For At least 2 hours!!"),
                ResponseData("Go To play", "Get In To Play Cricket For At least 2 hours!!"),
                ResponseData("Go To Gym", "Get To Gym For At least 1 hour!!"),
                ResponseData("Zoo", "Go to Zoo!!"),
                ResponseData("Code", "Code For At least 6 hours!!")
            )

            call.respond( a )
        }

        signIn(

            userDataSource = userDataSource,
            hashingService = hashingService,
            tokenService =tokenService,
            tokenConfig = tokenConfig

        )

        signUp(

            hashingService = hashingService,
            userDataSource = userDataSource,
            tokenService =tokenService,
            tokenConfig = tokenConfig

        )

        authenticate()

        getSecretInfo()

    }

}