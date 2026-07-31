package com.example.benchmark.microbenchmarks

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.benchmark.MLKEM
import com.example.benchmark.XWing
import com.example.benchmark.createLibSodium
import com.example.benchmark.utils.checkLibSodiumSuccess
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.goterl.lazysodium.interfaces.Box

/**
 * Benchmark, which will execute on an Android device.
 *
 * The body of [BenchmarkRule.measureRepeated] is measured in a loop, and Studio will
 * output the result. Modify your code to see how it affects performance.
 */


@RunWith(AndroidJUnit4::class)
class KeyGenBenchmark {
    private val libSodium = createLibSodium()
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun X25519KeyGen() {
        benchmarkRule.measureRepeated {
            val publicKey = ByteArray(Box.PUBLICKEYBYTES)
            val secretKey = ByteArray(Box.SECRETKEYBYTES)
            libSodium.randombytes_buf(/* buffer = */ secretKey, /* size = */ secretKey.size)
            val res = libSodium.crypto_scalarmult_base(publicKey, secretKey)
            // confirm successful keygen
            checkLibSodiumSuccess(res)
        }
    }

    @Test
    fun MLKEMKeyGen() {
        benchmarkRule.measureRepeated {
            val publicKey = ByteArray(MLKEM.PUBLICKEYBYTES)
            val secretKey = ByteArray(MLKEM.SECRETKEYBYTES)
            val res = libSodium.crypto_kem_mlkem768_keypair(publicKey, secretKey)
            // confirm successful keygen
            checkLibSodiumSuccess(res)
        }
    }

    @Test
    fun XwingKeyGen() {
        benchmarkRule.measureRepeated {
            val publicKey = ByteArray(XWing.PUBLICKEYBYTES)
            val secretKey = ByteArray(XWing.SECRETKEYBYTES)
            val res = libSodium.crypto_kem_keypair(publicKey, secretKey)
            // confirm successful keygen
            checkLibSodiumSuccess(res)
        }
    }
}

