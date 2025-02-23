package com.example

import com.example.security.hashing.SHA256HashingService
import com.example.security.token.JwtTokenService
import com.example.security.token.TokenConfig
import com.example.user.MongoUserDataSource
import com.mongodb.client.MongoDatabase
import io.ktor.server.application.*
import org.litote.kmongo.KMongo

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    configureSerialization()
    configureMonitoring()

    val database: MongoDatabase? = try {

        val connectionUri = environment.config.propertyOrNull("mongoDB.CONNECTION_STRING")?.getString()
        val dbName = environment.config.propertyOrNull("mongoDB.DATABASE_NAME")?.getString()

        if (connectionUri != null && dbName != null) {

            val client = KMongo.createClient(connectionUri)
            val db = client.getDatabase(dbName)

            environment.monitor.subscribe(ApplicationStopped) {

                client.close()

            }

            log.info("MongoDB connection successful")
            db

        } else {

            log.error("MongoDB connection failed: Missing configuration values")
            null

        }

    } catch (e: Exception) {

        log.error("MongoDB connection failed: ${e.message}", e)
        null

    }

    val userDataSource = database?.let { MongoUserDataSource( it ) }
    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(

        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expireIn = 30L * 1000L * 60L * 60L * 24L,
        secret = environment.config.property("jwt.JWT_SECRET").getString()

    )
    val hashingService = SHA256HashingService()

    configureSecurity( config = tokenConfig )


    if (userDataSource != null) {
        configureRouting(

            userDataSource = userDataSource,
            hashingService = hashingService,
            tokenService = tokenService,
            tokenConfig = tokenConfig

        )
    }

}
