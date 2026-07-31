package com.example.benchmark

import com.goterl.lazysodium.SodiumAndroid
import com.sun.jna.Native

class SodiumAndroidPQ: SodiumAndroid("mysodium"){
    //register my .so (1.0.22 not 1.0.20) files with SodiumAndroidPQ obj -> must be done in class initialiser
    init{Native.register(SodiumAndroidPQ::class.java, "mysodium")}

    //give kotlin versions of "public native int..." functions to X-wing
    external fun crypto_kem_keypair(publicKey: ByteArray, secretKey: ByteArray): Int
    external fun crypto_kem_enc(cipherText: ByteArray, sharedSecret: ByteArray, publicKey: ByteArray): Int
    external fun crypto_kem_dec(sharedSecret: ByteArray, cipherText: ByteArray, secretKey: ByteArray): Int

    // & ML-KEM

    external fun crypto_kem_mlkem768_keypair(publicKey: ByteArray, secretKey: ByteArray): Int

    external fun crypto_kem_mlkem768_enc(cipherText: ByteArray, sharedSecret: ByteArray, publicKey: ByteArray): Int

    external fun crypto_kem_mlkem768_dec(sharedSecret: ByteArray, cipherText: ByteArray, secretKey: ByteArray): Int
}

object XWing {
    const val PUBLICKEYBYTES = 1216
    const val SECRETKEYBYTES = 32
    const val CIPHERTEXTBYTES = 1120
    const val SHAREDSECRETBYTES = 32
}

object MLKEM {

    const val PUBLICKEYBYTES  = 1184

    const val SECRETKEYBYTES = 2400

    const val CIPHERTEXTBYTES = 1088

    const val SHAREDSECRETBYTES = 32
}