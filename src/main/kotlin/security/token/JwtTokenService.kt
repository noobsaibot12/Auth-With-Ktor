package com.example.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

class JwtTokenService: TokenService {

    override fun generate( config: TokenConfig, vararg claims: TokenClaim ): String {

        var token =  JWT.create()
            .withAudience( config.audience )
            .withIssuer( config.issuer )
            .withExpiresAt( Date( System.currentTimeMillis() + config.expireIn ) )

        claims.forEach { clam ->

            token = token.withClaim( clam.name , clam.value )

        }

        return token.sign( Algorithm.HMAC256( config.secret ) )

    }

}