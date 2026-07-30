package com.example.benchmark

import androidx.test.platform.app.InstrumentationRegistry
import com.example.benchmark.utils.checkLibSodiumSuccess
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import com.goterl.lazysodium.interfaces.Box

class ECDHTest {
    private val libSodium = createLibSodium()
    private val context = InstrumentationRegistry.getInstrumentation().context

    @Test
    fun X25519scalarmultWorksInBox() {
        // this just confirms it doesn't throw an error & comes out with the correct output, would be worth testing that the same test vector through my reconstructed box results in the same through the original
        // key gen 1
        val publicKey1 = ByteArray(Box.PUBLICKEYBYTES)
        val secretKey1 = ByteArray(Box.SECRETKEYBYTES)
        libSodium.randombytes_buf(/* buffer = */ secretKey1, /* size = */ secretKey1.size)
        val res = libSodium.crypto_scalarmult_base(publicKey1, secretKey1)
        // confirm successful keygen
        checkLibSodiumSuccess(res)

        // key gen 2
        val publicKey2 = ByteArray(Box.PUBLICKEYBYTES)
        val secretKey2 = ByteArray(Box.SECRETKEYBYTES)
        libSodium.randombytes_buf(/* buffer = */ secretKey2, /* size = */ secretKey2.size)
        val res1 = libSodium.crypto_scalarmult_base(publicKey2, secretKey2)
        // confirm successful keygen
        checkLibSodiumSuccess(res1)

        // encrypt
        val nonce = ByteArray(Box.NONCEBYTES)
        libSodium.randombytes_buf(/* buffer = */ nonce, /* size = */ nonce.size)

        val message = ByteArray(512)
        libSodium.randombytes_buf(/* buffer = */ message, /* size = */ message.size)
        val ciphertext = ByteArray(message.size + Box.MACBYTES)


        val res2 = libSodium.crypto_box_easy(
        /* cipherText = */ ciphertext,
        /* message = */ message,
        /* messageLen = */ message.size.toLong(),
        /* nonce = */ nonce,
        /* publicKey = */ publicKey2,
        /* secretKey = */ secretKey1)
        checkLibSodiumSuccess(res2)


        //decrypt
        val message1 = ByteArray(512)

        val res3 = libSodium.crypto_box_open_easy(
            message1,
            ciphertext,
            ciphertext.size.toLong(),
            nonce,
            publicKey1,
            secretKey2)
        checkLibSodiumSuccess(res3)

        assertThat(message).isEqualTo(message1)
    }

    @Test
    fun X25519scalarmultIsSameAsBoxKeys() {
        val publicKeyBox = ByteArray(Box.PUBLICKEYBYTES)
        val secretKey = ByteArray(Box.SECRETKEYBYTES)
        // randomise secret key
        libSodium.randombytes_buf(/* buffer = */ secretKey, /* size = */ secretKey.size)

        //keygen with box method
        val res = libSodium.crypto_box_keypair(publicKeyBox, secretKey)
        // confirm successful keygen
        checkLibSodiumSuccess(res)

        //re-keygen with scalarmult method
        val publicKeySM = ByteArray(Box.PUBLICKEYBYTES)
        val res2 = libSodium.crypto_scalarmult_base(publicKeySM, secretKey)
        // confirm successful keygen
        checkLibSodiumSuccess(res2)

        assertThat(publicKeySM).isEqualTo(publicKeyBox)
    }

}