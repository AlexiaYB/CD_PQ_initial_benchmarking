package com.example.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.benchmark.utils.checkLibSodiumSuccess
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.goterl.lazysodium.interfaces.DiffieHellman
import com.goterl.lazysodium.interfaces.Box

/**
 * Benchmark, which will execute on an Android device.
 *
 * The body of [BenchmarkRule.measureRepeated] is measured in a loop, and Studio will
 * output the result. Modify your code to see how it affects performance.
 */


@RunWith(AndroidJUnit4::class)
class EncapBenchmark {
    private val libSodium = createLibSodium()
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun X25519Encap() {
        benchmarkRule.measureRepeated {
            val publicKey = runWithMeasurementDisabled {ByteArray(Box.PUBLICKEYBYTES)}
            val secretKey = runWithMeasurementDisabled {ByteArray(Box.SECRETKEYBYTES)}
            runWithMeasurementDisabled {libSodium.randombytes_buf(/* buffer = */ secretKey, /* size = */ secretKey.size)}
            val res = runWithMeasurementDisabled {libSodium.crypto_scalarmult_base(publicKey, secretKey)}
            // confirm successful keygen
            runWithMeasurementDisabled {checkLibSodiumSuccess(res)}

            val sharedSecret = ByteArray(DiffieHellman.SCALARMULT_CURVE25519_BYTES)
            val res1 = libSodium.crypto_scalarmult(sharedSecret, secretKey, publicKey)
            checkLibSodiumSuccess(res1)
        }
    }

    @Test
    fun MLKEMEncap() {
        benchmarkRule.measureRepeated {
            val publicKey = runWithMeasurementDisabled { ByteArray(MLKEM.PUBLICKEYBYTES)}
            val secretKey = runWithMeasurementDisabled { ByteArray(MLKEM.SECRETKEYBYTES)}
            val res = runWithMeasurementDisabled { libSodium.crypto_kem_mlkem768_keypair(publicKey, secretKey)}
            // confirm successful keygen
            runWithMeasurementDisabled { checkLibSodiumSuccess(res)}

            val cipherText = ByteArray(MLKEM.CIPHERTEXTBYTES)
            val sharedSecret = ByteArray(MLKEM.CIPHERTEXTBYTES)
            val res1 = libSodium.crypto_kem_mlkem768_enc(cipherText,sharedSecret, publicKey)
            checkLibSodiumSuccess(res1)
        }
    }

    @Test
    fun XwingEncap() {
        benchmarkRule.measureRepeated {
            val publicKey = runWithMeasurementDisabled { ByteArray(XWing.PUBLICKEYBYTES)}
            val secretKey = runWithMeasurementDisabled { ByteArray(XWing.SECRETKEYBYTES)}
            val res = runWithMeasurementDisabled { libSodium.crypto_kem_keypair(publicKey, secretKey)}
            // confirm successful keygen
            runWithMeasurementDisabled {  checkLibSodiumSuccess(res)}

            val cipherText = ByteArray(XWing.CIPHERTEXTBYTES)
            val sharedSecret = ByteArray(XWing.CIPHERTEXTBYTES)
            val res1 = libSodium.crypto_kem_enc(cipherText,sharedSecret, publicKey)
            checkLibSodiumSuccess(res1)
        }
    }
}

