package com.lovisgod.payble_qpos_sdk

import com.lovisgod.kozen_p.PaybleConstants


object CardTypeUtils {

    private const val PATTERN_VISA = "^4[0-9]{12}(?:[0-9]{3})?$"
    private const val PATTERN_MASTERCARD = "^(?:5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12}$"
    private const val PATTERN_AMERICAN_EXPRESS = "^3[47][0-9]{13}$"
    private const val PATTERN_VERVE = "^((506([01]))|(507([89]))|(6500))[0-9]{12,15}$"
    private const val PATTERN_CUP = "^(62|88)\\d+\$"
    private const val PATTERN_DINERS_CLUB = "^3(?:0[0-5]|[68][0-9])[0-9]{11}$"
    private const val PATTERN_DISCOVER = "^6(?:011|5[0-9]{2})[0-9]{12}$"
    private const val PATTERN_JCB = "^(?:2131|1800|35[0-9]{3})[0-9]{11}$"
    private const val PATTERN_AFRIGO = "^5640\\d{10,15}$"


    fun getCardType(cardPan: String): PaybleConstants.CardType {
        if(PATTERN_VISA.toRegex().matches(cardPan)) return PaybleConstants.CardType.VISA
        if(PATTERN_MASTERCARD.toRegex().matches(cardPan)) return PaybleConstants.CardType.MASTER
        if(PATTERN_AMERICAN_EXPRESS.toRegex().matches(cardPan)) return PaybleConstants.CardType.AMERICANEXPRESS
        if(PATTERN_VERVE.toRegex().matches(cardPan)) return PaybleConstants.CardType.VERVE
        if(PATTERN_CUP.toRegex().matches(cardPan)) return PaybleConstants.CardType.CHINAUNIONPAY
        if(PATTERN_AFRIGO.toRegex().matches(cardPan)) return PaybleConstants.CardType.AFRIGO
        if(PATTERN_JCB.toRegex().matches(cardPan)) return PaybleConstants.CardType.JCB
        if(PATTERN_DISCOVER.toRegex().matches(cardPan)) return PaybleConstants.CardType.Discover
        if(PATTERN_DINERS_CLUB.toRegex().matches(cardPan)) return PaybleConstants.CardType.DinersClub

        return PaybleConstants.CardType.None
    }

    fun getAcquirerID(cardPan: String): String {
        return when(getCardType(cardPan)) {
            PaybleConstants.CardType.AFRIGO -> cardPan.substring(0, 6)
            else -> cardPan.substring(0, 6)
        }
    }
}