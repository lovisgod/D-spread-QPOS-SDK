package com.lovisgod.payble_qpos_sdk.utils

import java.math.BigInteger
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

fun fromHex(c: Char): Int {
    return when {
        c in '0'..'9' -> c.code - '0'.code
        c in 'A'..'F' -> c.code - 'A'.code + 10
        c in 'a'..'f' -> c.code - 'a'.code + 10
        else -> throw IllegalArgumentException("Invalid hex character")
    }
}

fun toHex(nybble: Int): Char {
    require(nybble in 0..15) { "Invalid nybble value" }
    return "0123456789ABCDEF"[nybble]
}

fun xorHex(a: String, b: String): String {
    val chars = a.indices.map { toHex(fromHex(a[it]) xor fromHex(b[it])) }
    return chars.joinToString("").uppercase()
}

fun newXorHex(pinBlock: String, panBlock: String): String {
    val pinBlockBigInt = BigInteger(pinBlock, 16)
    val panBlockBigInt = BigInteger(panBlock, 16)
    return "0${(pinBlockBigInt xor panBlockBigInt).toString(16)}"
}

fun generatePinBlock(pin: String, cardNumber: String): String {
    if (pin.length !in 4..6) throw Exception("Invalid pin length")

    var pinBlock = "0${pin.length}$pin"
    while (pinBlock.length != 16) {
        pinBlock += "F"
    }

    val maxPanLength = 12
    val cardLen = cardNumber.length
    val pan = "0000${cardNumber.substring(cardLen - maxPanLength - 1, cardLen - 1)}"
    return newXorHex(pinBlock, pan)
}

fun getFinalFormat0Pin(key: String, xoredPinBlock: String): String {
    val uint8listPin = parseHexStr2Byte(xoredPinBlock)
    val keyBytes = parseHexStr2Byte(key)
    val resultxx = triDesEncryption(keyBytes, uint8listPin)
    return parseByte2HexStr(resultxx!!)
}

fun getPinBlock0(pan: String, key: String, clearPin: String): String {
    val xoreString = generatePinBlock(clearPin, pan)
    println("The Xore string is $xoreString")
    val finalPinBlock = getFinalFormat0Pin(key, xoreString)
    return finalPinBlock
}

// Helper functions to replace the equivalent in the Dart code
fun parseHexStr2Byte(hexStr: String): ByteArray {
    val len = hexStr.length
    val data = ByteArray(len / 2)
    var i = 0
    while (i < len) {
        data[i / 2] = ((Character.digit(hexStr[i], 16) shl 4) + Character.digit(hexStr[i + 1], 16)).toByte()
        i += 2
    }
    return data
}

fun parseByte2HexStr(bytes: ByteArray): String {
    val hexString = StringBuilder()
    for (b in bytes) {
        val hex = Integer.toHexString(0xFF and b.toInt())
        if (hex.length == 1) {
            hexString.append('0')
        }
        hexString.append(hex)
    }
    return hexString.toString().uppercase()
}

fun triDesEncryption(byteKey: ByteArray, dec: ByteArray): ByteArray? {
    return try {
        val enKey = when (byteKey.size) {
            16 -> byteKey + byteKey.copyOfRange(0, 8)
            8 -> byteKey + byteKey + byteKey
            else -> byteKey
        }

        val keySpec = SecretKeySpec(enKey, "DESede")
        val cipher = Cipher.getInstance("DESede/ECB/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        cipher.doFinal(dec)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
