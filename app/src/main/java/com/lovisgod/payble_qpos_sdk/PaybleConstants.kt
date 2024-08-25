package com.lovisgod.kozen_p

import com.dspread.xpos.QPOSService

object PaybleConstants {

    const val PINKEY_INDEX = 2
    const val DUKPTKEY_INDEX = 1
    const val KEY_LOAD_ERROR = 99

    const val EMV_CARD_NOT = 0
    const val EMV_CARD_VISA = 1
    const val EMV_CARD_UNIONPAY = 2
    const val EMV_CARD_MASTERCARD = 3
    const val EMV_CARD_DISCOVER = 4
    const val EMV_CARD_AMEX = 5
    const val EMV_CARD_JCB = 6
    const val EMV_CARD_MIR = 7
    const val EMV_CARD_RUPAY = 8
    const val EMV_CARD_PURE = 9
    const val EMV_CARD_INTERAC = 10
    const val EMV_CARD_EFTPOS = 11


    var transactionType: QPOSService.TransactionType = QPOSService.TransactionType.GOODS
    var transactionTypeString : String  = "GOODS"

    var transAmount = "0"
    var currencyCode = "566"
    var api_key = ""
    var mid = ""



    enum class CardType( val code: String) {
        MASTER("Master"),
        VISA("Visa"),
        VERVE("Verve"),
        AMERICANEXPRESS("AMEX"),
        CHINAUNIONPAY("CUP"),
        AFRIGO("Afrigo"),
        JCB("JCB"),
        Discover("Discover"),
        DinersClub("Diner'sClub"),
        None("None"),
        Unknown("Unknown")
    }

    const val TRANSACTION_AMOUNT = "TRANSACTION_AMOUNT"
    const val ANOTHER_AMOUNT = "ANOTHER_AMOUNT"
    const val APPLICATION_INTERCHANGE_PROFILE = "APPLICATION_INTERCHANGE_PROFILE"
    const val APPLICATION_TRANSACTION_COUNTER = "APPLICATION_TRANSACTION_COUNTER"
    const val AUTHORIZATION_REQUEST = "AUTHORIZATION_REQUEST"
    const val CRYPTOGRAM_INFO_DATA = "CRYPTOGRAM_INFO_DATA"
    const val CARD_HOLDER_VERIFICATION_RESULT = "CARD_HOLDER_VERIFICATION_RESULT"
    const val ISSUER_APP_DATA = "ISSUER_APP_DATA"
    const val TRANSACTION_CURRENCY_CODE = "TRANSACTION_CURRENCY_CODE"
    const val TERMINAL_VERIFICATION_RESULT = "TERMINAL_VERIFICATION_RESULT"
    const val TERMINAL_COUNTRY_CODE = "TERMINAL_COUNTRY_CODE"
    const val TERMINAL_TYPE = "TERMINAL_TYPE"
    const val TERMINAL_CAPABILITIES = "TERMINAL_CAPABILITIES"
    const val TRANSACTION_DATE = "TRANSACTION_DATE"
    const val TRANSACTION_TYPE = "TRANSACTION_TYPE"
    const val UNPREDICTABLE_NUMBER = "UNPREDICTABLE_NUMBER"
    const val DEDICATED_FILE_NAME = "DEDICATED_FILE_NAME"
    const val ICC_STRING = "ICC_STRING"
    const val INTERFACE_DEVICE_SERIAL_NUMBER = "INTERFACE_DEVICE_SERIAL_NUMBER"
    const val APP_VERSION_NUMBER = "APP_VERSION_NUMBER"
    const val APP_PAN_SEQUENCE_NUMBER = "APP_PAN_SEQUENCE_NUMBER"
    const val CARD_HOLDER_NAME = "CARD_HOLDER_NAME"
    const val PIN_BLOCK = "PIN_BLOCK"
    const val KSN = "KSN"
    const val TRACK_2_DATA = "TRACK_2_DATA"
}