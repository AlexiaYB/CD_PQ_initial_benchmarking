package com.example.benchmark.ownbenchmarks

import org.junit.Test
import android.util.Log
import com.example.benchmark.MLKEM
import com.example.benchmark.XWing
import com.example.benchmark.createLibSodium
import com.example.benchmark.utils.checkLibSodiumSuccess
import com.goterl.lazysodium.interfaces.Box
import com.goterl.lazysodium.interfaces.DiffieHellman
import java.time.Duration
import com.google.common.truth.Truth.assertThat

private const val BENCHMARK_TAG = "Benchmark"


class DecapOwnBenchmark {
    val libSodium = createLibSodium()

    @Test
    fun combined() {
        // X25519 keygen & encaps
        val X25519publicKey1 = ByteArray(Box.PUBLICKEYBYTES)
        val X25519secretKey1 = ByteArray(Box.SECRETKEYBYTES)
        libSodium.randombytes_buf(X25519secretKey1, X25519secretKey1.size)
        val res = libSodium.crypto_scalarmult_base(X25519publicKey1, X25519secretKey1)
        // confirm successful keygen
        checkLibSodiumSuccess(res)

        val X25519publicKey2 = ByteArray(Box.PUBLICKEYBYTES)
        val X25519secretKey2 = ByteArray(Box.SECRETKEYBYTES)
        libSodium.randombytes_buf(X25519secretKey2, X25519secretKey2.size)
        val res6 = libSodium.crypto_scalarmult_base(X25519publicKey2, X25519secretKey2)
        // confirm successful keygen
        checkLibSodiumSuccess(res6)

        val X25519sharedSecret1 = ByteArray(DiffieHellman.SCALARMULT_CURVE25519_BYTES)
        val res3 =
            libSodium.crypto_scalarmult(X25519sharedSecret1, X25519secretKey1, X25519publicKey2)
        checkLibSodiumSuccess(res3)

        // MLKEM keygen & encaps
        val MLKEMpublicKey = ByteArray(MLKEM.PUBLICKEYBYTES)
        val MLKEMsecretKey = ByteArray(MLKEM.SECRETKEYBYTES)
        val res1 = libSodium.crypto_kem_mlkem768_keypair(MLKEMpublicKey, MLKEMsecretKey)
        // confirm successful keygen
        checkLibSodiumSuccess(res1)

        val MLKEMcipherText = ByteArray(MLKEM.CIPHERTEXTBYTES)
        val MLKEMsharedSecret1 = ByteArray(MLKEM.SHAREDSECRETBYTES)
        val res4 = libSodium.crypto_kem_mlkem768_enc(MLKEMcipherText, MLKEMsharedSecret1, MLKEMpublicKey)
        checkLibSodiumSuccess(res4)

        // Xwing keygen & encaps
        val XwingpublicKey = ByteArray(XWing.PUBLICKEYBYTES)
        val XwingsecretKey = ByteArray(XWing.SECRETKEYBYTES)
        val res2 = libSodium.crypto_kem_keypair(XwingpublicKey, XwingsecretKey)
        // confirm successful keygen
        checkLibSodiumSuccess(res2)

        val XwingcipherText = ByteArray(XWing.CIPHERTEXTBYTES)
        val XwingsharedSecret1 = ByteArray(XWing.SHAREDSECRETBYTES)
        val res5 = libSodium.crypto_kem_enc(XwingcipherText, XwingsharedSecret1, XwingpublicKey)
        checkLibSodiumSuccess(res5)



        for (i in 1..10) {
            Log.i(BENCHMARK_TAG, "run $i")
            Log.i(BENCHMARK_TAG, "X25519")
            runBenchmark(100) {
                val X25519sharedSecret2 = ByteArray(DiffieHellman.SCALARMULT_CURVE25519_BYTES)
                val res7 =
                    libSodium.crypto_scalarmult(
                        X25519sharedSecret2,
                        X25519secretKey2,
                        X25519publicKey1
                    )
                checkLibSodiumSuccess(res7)
//                assertThat(X25519sharedSecret1).isEqualTo(X25519sharedSecret2)
            }

            Log.i(BENCHMARK_TAG, "ML-KEM")
            runBenchmark(100) {
                val MLKEMsharedSecret2 = ByteArray(MLKEM.SHAREDSECRETBYTES)
                val res8 = libSodium.crypto_kem_mlkem768_dec(
                    MLKEMsharedSecret2,
                    MLKEMcipherText,
                    MLKEMsecretKey
                )
                checkLibSodiumSuccess(res8)
//                assertThat(MLKEMsharedSecret1).isEqualTo(MLKEMsharedSecret2)

            }

            Log.i(BENCHMARK_TAG, "Xwing")
            runBenchmark(100) {
                val XwingsharedSecret2 = ByteArray(XWing.SHAREDSECRETBYTES)
                val res9 = libSodium.crypto_kem_dec(
                    XwingsharedSecret2,
                    XwingcipherText,
                    XwingsecretKey
                )
                checkLibSodiumSuccess(res9)
//                assertThat(XwingsharedSecret1).isEqualTo(XwingsharedSecret2)

            }
        }
    }


    private fun runBenchmark(iterations: Int, operation: () -> Unit) {
//        val warmUp = 10
//        repeat(warmUp) {
//            operation()
//        }

        val timeStart = System.nanoTime()

        repeat(iterations) {
            operation()
        }

        val timeEnd = System.nanoTime()
        val difference = Duration.ofNanos(timeEnd - timeStart)

//        Log.i(BENCHMARK_TAG, "iterations: $iterations")
//        Log.i(BENCHMARK_TAG, "total: ${difference.toNanos()} ns")

        val individualTimeMs = difference.toNanos().toFloat() / iterations
        Log.i(BENCHMARK_TAG, "per: $individualTimeMs ns")
    }
}