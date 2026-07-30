package com.example.benchmark

import androidx.test.platform.app.InstrumentationRegistry
import com.example.benchmark.utils.checkLibSodiumSuccess
import org.junit.Test
import com.google.common.truth.Truth.assertThat

class KEMTest {
    private val libSodium = createLibSodium()
    private val context = InstrumentationRegistry.getInstrumentation().context

    @Test
    fun testXwingEncapAndDecapMatch() {
        // key gen
        val publicKey = ByteArray(XWing.PUBLICKEYBYTES)
        val secretKey = ByteArray(XWing.SECRETKEYBYTES)
        val res = libSodium.crypto_kem_keypair(publicKey, secretKey)
        // confirm successful keygen
        checkLibSodiumSuccess(res)

        // encaps
        val cipherText = ByteArray(XWing.CIPHERTEXTBYTES)
        val sharedSecret = ByteArray(XWing.SHAREDSECRETBYTES)
        val res2 = libSodium.crypto_kem_enc(cipherText, sharedSecret, publicKey)
        // confirm successful encaps
        checkLibSodiumSuccess(res2)

        //decaps
        val sharedSecretDecaps = ByteArray(XWing.SHAREDSECRETBYTES)
        val res3 = libSodium.crypto_kem_dec(sharedSecretDecaps, cipherText, secretKey)
        // confirm successful decaps
        checkLibSodiumSuccess(res3)

        //confirm both shared secrets are equal
        assertThat(sharedSecret).isEqualTo(sharedSecretDecaps)
    }

    @Test
    fun testMLKEMEncapAndDecapMatch() {
        // key gen
        val publicKey = ByteArray(MLKEM.PUBLICKEYBYTES)
        val secretKey = ByteArray(MLKEM.SECRETKEYBYTES)
        val res = libSodium.crypto_kem_mlkem768_keypair(publicKey, secretKey)
        // confirm successful keygen
        checkLibSodiumSuccess(res)

        // encaps
        val cipherText = ByteArray(MLKEM.CIPHERTEXTBYTES)
        val sharedSecret = ByteArray(MLKEM.SHAREDSECRETBYTES)
        val res2 = libSodium.crypto_kem_mlkem768_enc(cipherText, sharedSecret, publicKey)
        // confirm successful encaps
        checkLibSodiumSuccess(res2)

        //decaps
        val sharedSecretDecaps = ByteArray(MLKEM.SHAREDSECRETBYTES)
        val res3 = libSodium.crypto_kem_mlkem768_dec(sharedSecretDecaps, cipherText, secretKey)
        // confirm successful decaps
        checkLibSodiumSuccess(res3)

        //confirm both shared secrets are equal
        assertThat(sharedSecret).isEqualTo(sharedSecretDecaps)
    }


}