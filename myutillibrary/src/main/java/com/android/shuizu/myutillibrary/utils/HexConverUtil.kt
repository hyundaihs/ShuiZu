package com.android.shuizu.myutillibrary.utils

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/9/5/005.
 */

fun Any.charToHexInt(c: Char): Int {
    return intToHexInt(c.toInt())
}

fun Any.charToHexStr(c: Char): String {
    return intToHexStr(c.toInt())
}

fun Any.byteToHexInt(b: Byte): Int {
    return intToHexInt(b.toInt())
}

fun Any.byteToHexStr(b: Byte): String {
    return intToHexStr(b.toInt())
}

fun Any.intToHexInt(i: Int): Int {
    return Integer.valueOf(intToHexStr(i),16)
}

fun Any.intToHexStr(i: Int): String {
    return Integer.toHexString(i)
}

fun makeChecksum(hex: String): String {
    var hexdata = hex
    if (hexdata == "") {
        return "00"
    }
    hexdata = hexdata.replace(" ".toRegex(), "")
    var total = 0
    val len = hexdata.length
    if (len % 2 != 0) {
        return "00"
    }
    var num = 0
    while (num < len) {
        val s = hexdata.substring(num, num + 2)
        total += Integer.parseInt(s, 16)
        num += 2
    }
    return format(hexInt(total).toInt(16) % 256)
}

private fun hexInt(total: Int): String {
    val a = total / 256
    val b = total % 256
    return if (a > 255) {
        hexInt(a) + format(b)
    } else format(a) + format(b)
}

private fun format(hex: Int): String {
    var hexa = Integer.toHexString(hex)
    val len = hexa.length
    if (len < 2) {
        hexa = "0$hexa"
    }
    return hexa
}