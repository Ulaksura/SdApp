package com.example.sdapp.security

import android.os.Build
import java.security.SecureRandom
import java.security.spec.KeySpec
import java.util.HexFormat
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

fun generateRandomSalt(): String {
    val random = SecureRandom()
    val salt = ByteArray(16)
    random.nextBytes(salt)
    return salt.toHexString()
}
fun ByteArray.toHexString(): String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        HexFormat.of().formatHex(this)
    } else {
        joinToString("") { "%02x".format(it) }
    }

private const val ALGORITHM = "PBKDF2WithHmacSHA512"
private const val ITERATIONS = 120_000
private const val KEY_LENGTH = 256
private const val SECRET = "SomeRandomSecret"

fun generateHash(password: String, salt: String): String {
    val combinedSalt = "$salt$SECRET".toByteArray()
    val factory: SecretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM)
    val spec: KeySpec = PBEKeySpec(password.toCharArray(), combinedSalt, ITERATIONS, KEY_LENGTH)
    val key: SecretKey = factory.generateSecret(spec)
    val hash: ByteArray = key.encoded
    return hash.toHexString()
}