package com.example.security.hashing

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.security.MessageDigest
import java.security.SecureRandom

class SHA256HashingService: HashingService {

    override fun generateSaltedHash(value: String, saltLength: Int): SaltedHash {

        val salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLength)
        val saltAsHex = Hex.encodeHexString(salt)  // Convert bytes to hex string
        val hash = DigestUtils.sha256Hex(saltAsHex + value) // Hash with hex salt

        return SaltedHash(

            hash = hash,
            salt = saltAsHex

        )

    }

    override fun verify(value: String, saltedHash: SaltedHash): Boolean {

        val log: Logger = LoggerFactory.getLogger("Application")

        val computedHash = DigestUtils.sha256Hex(saltedHash.salt + value) // Use stored salt (already hex)
        log.info("Computed Hash from Input Password: $computedHash")
        log.info("Stored Hash: ${saltedHash.hash}")

        return computedHash == saltedHash.hash

    }

    private fun hashWithSalt(value: String, salt: String): String {

        return DigestUtils.sha256Hex("$salt$value")

    }

}