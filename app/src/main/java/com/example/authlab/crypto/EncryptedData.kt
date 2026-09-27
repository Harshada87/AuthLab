package com.example.authlab.crypto

data class EncryptedData(
    val ciphertext: ByteArray,
    val iv: ByteArray   //"initialization vector" - needed later to decrypt
)
