package com.example

import com.example.requests.AuthRequest
import com.example.response.AuthResponse
import com.example.security.hashing.HashingService
import com.example.security.hashing.SaltedHash
import com.example.security.token.TokenClaim
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import com.example.user.User
import com.example.user.UserDataSource
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Route.signUp(

    hashingService: HashingService,
    userDataSource: UserDataSource,
    tokenService: TokenService,
    tokenConfig: TokenConfig

) {

    post ( "/signup" ) {

        val request = call.receive<AuthRequest>()

//        val contentType = call.request.contentType()
//        log.info("MongoDB connection successful")

        val areFieldsBlank = request.email.isBlank() || request.password.isBlank()
        val isPwTooShort = request.password.length < 8

        if ( areFieldsBlank || isPwTooShort ) {

            call.respond(HttpStatusCode.Conflict)
            return@post

        }

        val saltedHash = hashingService.generateSaltedHash( request.password )
        val user = User(

            email = request.email,
            password = saltedHash.hash,
            salt = saltedHash.salt

        )
        val wasAcknowledged = userDataSource.insertUser( user )

        if ( !wasAcknowledged ) {

            call.respond( HttpStatusCode.Conflict )
            return@post

        }

        val token = tokenService.generate(

            config = tokenConfig,
            TokenClaim(

                name = "userId",
                value = user.id.toString()

            )

        )

        call.respond(

            HttpStatusCode.OK ,
            AuthResponse(

                token = token

            )

        )

    }

}

fun Route.signIn (

    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig

) {

     val log: Logger = LoggerFactory.getLogger("Application")

    post ("/signIn") {

        val request = call.receive<AuthRequest>()

        val user = userDataSource.getUserByEmail( request.email )

        if ( user == null ) {

            call.respond( HttpStatusCode.Conflict , "Incorrect Email" )
            return@post

        }

        log.info("Received Password: $user")
        log.info("Stored Hash: ${user.password}")
        log.info("Stored Salt: ${user.salt}")

        val isValidPassword = hashingService.verify(

            value = request.password,
            saltedHash = SaltedHash(

                hash = user.password,
                salt = user.salt

            )

        )

        log.info("Received Password: ${request.password}")
        log.info("Stored Hash: ${user.password}")
        log.info("Stored Salt: ${user.salt}")
        log.info("Verification Result: $isValidPassword")

        if ( !isValidPassword ) {

            call.respond( HttpStatusCode.Conflict , "Incorrect Password!!" )
            return@post

        }

        val token = tokenService.generate(

            config = tokenConfig,
            TokenClaim(

                name = "userId",
                value = user.id.toString()

            )

        )

        call.respond(

            status = HttpStatusCode.OK,
            message = AuthResponse(

                token = token

            )

        )

    }

}

fun Route.authenticate() {

    authenticate("auth-jwt") {

        get("authenticate") {

            call.respond(HttpStatusCode.OK)

        }

    }

}

fun Route.getSecretInfo() {

    authenticate ( "auth-jwt" ) {

        get ( "secret" ) {
    
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim( "userId" , String::class )
            call.respond( HttpStatusCode.OK , "Your userId is : $userId" )

        }

    }

}