package com.example.benchmark.utils

internal fun checkLibSodiumSuccess(res: Int) {
    check(res == 0) { "lib-sodium call failed: res=$res" }
}