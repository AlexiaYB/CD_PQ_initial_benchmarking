package com.example.benchmark.ownbenchmarks

import org.junit.Test
import android.util.Log
import com.example.benchmark.MLKEM
import com.example.benchmark.XWing
import com.example.benchmark.createLibSodium
import com.example.benchmark.utils.checkLibSodiumSuccess
import com.goterl.lazysodium.interfaces.Box
import java.time.Duration

private const val BENCHMARK_TAG = "Benchmark"

class KeyGenOwnBenchmark {
    val libSodium = createLibSodium()

    @Test
    fun Xwing() {
        Log.i(BENCHMARK_TAG, "Xwing Key Gen Own")
        for(i in 1..10){
            Log.i(BENCHMARK_TAG, "run $i")
            runBenchmark(100) {
                val publicKey = ByteArray(XWing.PUBLICKEYBYTES)
                val secretKey = ByteArray(XWing.SECRETKEYBYTES)
                val res = libSodium.crypto_kem_keypair(publicKey, secretKey)
                // confirm successful keygen
                checkLibSodiumSuccess(res)
            }
        }
    }

    @Test
    fun MLKEM() {
        Log.i(BENCHMARK_TAG, "MLKEM Key Gen Own")
        for(i in 1..10){
            Log.i(BENCHMARK_TAG, "run $i")
            runBenchmark(100) {
                val publicKey = ByteArray(MLKEM.PUBLICKEYBYTES)
                val secretKey = ByteArray(MLKEM.SECRETKEYBYTES)
                val res = libSodium.crypto_kem_mlkem768_keypair(publicKey, secretKey)
                // confirm successful keygen
                checkLibSodiumSuccess(res)
            }
        }
    }

    @Test
    fun X25519() {
        Log.i(BENCHMARK_TAG, "X25519 Key Gen Own")
        for(i in 1..10){
            Log.i(BENCHMARK_TAG, "run $i")
            runBenchmark(100) {
                val publicKey = ByteArray(Box.PUBLICKEYBYTES)
                val secretKey = ByteArray(Box.SECRETKEYBYTES)
                libSodium.randombytes_buf(secretKey, secretKey.size)
                val res1 = libSodium.crypto_scalarmult_base(publicKey, secretKey)
                // confirm successful keygen
                checkLibSodiumSuccess(res1)
            }
        }
    }

    @Test
    fun combined() {
        for(i in 1..10) {
            Log.i(BENCHMARK_TAG, "run $i")
            Log.i(BENCHMARK_TAG, "X25519")
            runBenchmark(100) {
                val publicKey = ByteArray(Box.PUBLICKEYBYTES)
                val secretKey = ByteArray(Box.SECRETKEYBYTES)
                libSodium.randombytes_buf(secretKey, secretKey.size)
                val res1 = libSodium.crypto_scalarmult_base(publicKey, secretKey)
                // confirm successful keygen
                checkLibSodiumSuccess(res1)

            }
            Log.i(BENCHMARK_TAG, "ML-KEM")
            runBenchmark(100) {
                val publicKey = ByteArray(MLKEM.PUBLICKEYBYTES)
                val secretKey = ByteArray(MLKEM.SECRETKEYBYTES)
                val res = libSodium.crypto_kem_mlkem768_keypair(publicKey, secretKey)
                // confirm successful keygen
                checkLibSodiumSuccess(res)
            }

            Log.i(BENCHMARK_TAG, "Xwing")
            runBenchmark(100) {
                val publicKey = ByteArray(XWing.PUBLICKEYBYTES)
                val secretKey = ByteArray(XWing.SECRETKEYBYTES)
                val res = libSodium.crypto_kem_keypair(publicKey, secretKey)
                // confirm successful keygen
                checkLibSodiumSuccess(res)
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