package com.example.benchmark

internal fun createLibSodium(): SodiumAndroidPQ {
    val sodium = SodiumAndroidPQ()
    sodium.sodium_init()
    return sodium
}

