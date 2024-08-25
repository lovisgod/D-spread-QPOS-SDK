package com.lovisgod.payble_qpos_sdk.utils

import android.util.Log

class TLVHelper {

    var c0Value: String? = null
        private set
    var c1Value: String? = null
        private set
    var c2Value: String? = null
        private set
    var c7Value: String? = null
        private set
    var realPan: String? = null
        private set
    var pinBlock: String? = null
        private set
    var panSequenceNumber: String? = null
        private set
    var track2Data: String? = null
        private set
    var iccData: String? = null
        private set
    var cardHolderName: String? = null
        private set
    var cardType: String? = null
        private set
    var cardExpiry: String? = null
        private set
    var cardPan: String? = null
        private set

    var iccString: String? = null
        private set

    fun parseTLVData(value: String) {
//        val tlvData = value["tlv"] as? String
        val tlvData = value
        if (tlvData != null) {
            Log.d("TLVHelper", "tlv=$tlvData")
            val parsedTLVData = tlvData.replaceFirst("onRequestOnlineProcess:", "")
            Log.d("TLVHelper", "newtlv=$parsedTLVData")
            Log.d("TLVHelper", "value=$value")

            val tlvList = TLVParser.parse(parsedTLVData)

            for (tlv in tlvList) {
                println(tlv)
                when (tlv.tag) {
                    "c2" -> c2Value = tlv.value
                    "c1" -> c1Value = tlv.value
                    "c7" -> c7Value = tlv.value
                    "c0" -> c0Value = tlv.value
                }
            }

            val onlineMessageTLV = TLVParser.parse(getData(c0Value!!, c2Value!!, DUKPK2009_CBC.Enum_key.DATA, DUKPK2009_CBC.Enum_mode.CBC, null))
            val pin = getData(c0Value!!, c2Value!!, DUKPK2009_CBC.Enum_key.PIN, DUKPK2009_CBC.Enum_mode.CBC, null)
            val data = getData(c0Value!!, c2Value!!, DUKPK2009_CBC.Enum_key.DATA, DUKPK2009_CBC.Enum_mode.CBC, null)

            Log.d("TLVHelper", "data icc is ${data.substring(2)}")
            Log.d("TLVHelper", "pin icc data is $pin")

            for (tlv in onlineMessageTLV) {
                when (tlv.tag) {
                    "5a" -> {
                        realPan = tlv.value
                        val parsCarN = realPan!!.split("f")[0]
                        Log.d("TLVHelper", "Parscarn string $parsCarN")
                        Log.d("TLVHelper", "c7value is generated $c7Value")

                        pinBlock = if (c7Value != null) {
//                            getPinBlock(parsCarN, "", "")
                            c7Value
                        } else {
                            null
                        }
                        Log.d("TLVHelper", "pinBlock string $pinBlock")
                    }
                    "5f34" -> panSequenceNumber = tlv.value
                    "57" -> track2Data = tlv.value
                    "9f4c" -> iccData = tlv.value
                    "5f20" -> {
                        cardHolderName = hexToAscii(tlv.value)
                        Log.d("TLVHelper", "Card Holder name is $cardHolderName")
                    }
                    "50" -> {
                        cardType = hexToAscii(tlv.value)
                        Log.d("TLVHelper", "The Card type is $cardType")
                    }
                    "5f24" -> {
                        cardExpiry = formatNumberAsDate(tlv.value)
                        Log.d("TLVHelper", "The card expiry date is $cardExpiry")
                    }
                    "5a" -> {
                        cardPan = maskDigits(tlv.value)
                        Log.d("TLVHelper", "The masked Pan is $cardPan")
                    }
                }
                Log.d("TLVHelper", "online message $tlv")
            }

            Log.d("TLVHelper", "get data ${getData(c0Value!!, c2Value!!, DUKPK2009_CBC.Enum_key.DATA, DUKPK2009_CBC.Enum_mode.CBC, null)}")

            val tlvx = StringBuilder()
            val tagValues = mutableListOf<Pair<String, String>>()

            for (tag in REQUEST_TAGS) {
                for (element in onlineMessageTLV) {
                    if (tag.tag == element.tag.toUpperCase()) {
                        tlvx.append("${element.tag}${element.length}${element.value}")
                        Log.d("TLVHelper", "request tag ${tag.tag} value ${element.value}")

                        tagValues.add(tag.tag to element.value)
                    }
                }
                Log.d("TLVHelper", "Tlvx : ${tlvx.toString().toUpperCase()}")
                iccString += tlvx.toString().uppercase()
            }
            Log.d("TLVHelper", "The tag values are $tagValues")
        }
    }

    private fun getData(c0Value: String, c2Value: String, key: DUKPK2009_CBC.Enum_key, mode: DUKPK2009_CBC.Enum_mode, something: Any?): String {
        return DUKPK2009_CBC.getData(c0Value, c2Value, key, mode)
    }

    private fun getPinBlock(parsCarN: String, pinKey: String, clearPinText: String): String {
        return ""
    }

    private fun hexToAscii(hexString: String): String {
        val buffer = StringBuilder()
        for (i in hexString.indices step 2) {
            val hexChar = hexString.substring(i, i + 2)
            val charCode = hexChar.toInt(16)
            buffer.append(charCode.toChar())
        }
        return buffer.toString()
    }


    private fun formatNumberAsDate(numberString: String): String {
        // Ensure the string has at least 4 characters
        if (numberString.length < 4) {
            return "Invalid string"
        }

        // Take the first two characters as the day and the next two as the month
        var day = numberString.substring(0, 2)
        var month = numberString.substring(2, 4)

        // Pad day and month with leading zeros if needed
        day = day.padStart(2, '0')
        month = month.padStart(2, '0')

        // Format as MM/DD
        return "$month/$day"
    }


    private fun maskDigits(fullNumber: String): String {
        // Ensure the number has at least 16 digits
        if (fullNumber.length < 16) {
            return fullNumber // Do nothing if the number is too short
        }

        // Extract the first 4 digits
        val firstDigits = fullNumber.substring(0, 4)

        // Extract the last 4 digits
        val lastDigits = fullNumber.substring(fullNumber.length - 4)

        // Create a masked string with asterisks (*) for the middle 8 digits
        val maskedMiddleDigits = "********"

        // Concatenate the first, masked middle, and last digits
        return "$firstDigits$maskedMiddleDigits$lastDigits"
    }


}
