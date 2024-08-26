package com.lovisgod.payble_qpos_sdk.utils

import com.lovisgod.payble_qpos_sdk.CardTypeUtils

class CardTransactionData {

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

    fun setValuesFromMap(mapData: Map<String, String>) {
        val track2Datax = mapData["encTrack2"]?.let { Track2DataHelper.create( track2Data = it) }
        c0Value = mapData["iccCardAppexpiryDate"]
        c1Value = mapData["pinKsn"]
        c2Value = mapData["serviceCode"]
        c7Value = mapData["pinBlock"]
        realPan = track2Datax?.panData
        pinBlock = mapData["pinBlock"]
        panSequenceNumber = mapData["cardSquNo"]
        track2Data = track2Datax?.track2
        iccData = mapData["iccdata"]
        cardHolderName = mapData["cardholderName"]
        cardType = CardTypeUtils.getCardType(track2Datax?.panData.toString()).name
        cardExpiry = track2Datax?.cardExpiry
        cardPan = track2Datax?.panData
        iccString = mapData["iccdata"]  // Or any other relevant value
    }

}

//fun main() {
//    val mapData = mapOf(
//        "iccCardAppexpiryDate" to "5F3401015F24032903319F1E083132383030323538500556657276659F120556657276654F07A0000003710001",
//        "encTrack3" to "",
//        "pinKsn" to "56623112800258E00026",
//        "pinRandomNumber" to "",
//        "encTrack2" to "5078721100653689D2903601016593342FFFFFFFFFFFFFFF",
//        "encTrack1" to "",
//        "track3Length" to "0",
//        "pinBlock" to "5371A89B076262E2",
//        "serviceCode" to "601",
//        "maskedPAN" to "507872XXXXXX3689",
//        "cardholderName" to "OLOSUNDE/AYOOLUWA",
//        "formatID" to "38",
//        "psamNo" to "",
//        "posID" to "",
//        "encPAN" to "",
//        "encTracks" to "5078721100653689D2903601016593342FFFFFFFFFFFFFFF",
//        "trackRandomNumber" to "",
//        "track2Length" to "33",
//        "cardSquNo" to "",
//        "iccdata" to "9F260816D06969A9F4C63C9F2701809F10200FA501A231F8040000000000000000000F0000000000000000000000000000009F3704E66EB0CF9F360201A9950504800480009A032408259C01009F02060000000100005F2A020566820239009F1A0205669F03060000000000009F3303E0F8C89F34034203009F3501229F1E0831323830303235388407A00000037100019F090200969F410400000058",
//        "track1Length" to "0",
//        "trackksn" to "",
//        "newPinBlock" to "",
//        "expiryDate" to "015F24"
//    )
//
//    val cardTransactionData = CardTransactionData()
//    cardTransactionData.setValuesFromMap(mapData)
//
//    // Now you can access the values from cardTransactionData object
//    println("Card Holder Name: ${cardTransactionData.cardHolderName}")
//    println("PIN Block: ${cardTransactionData.pinBlock}")
//    println("ICC Data: ${cardTransactionData.iccData}")
//}
