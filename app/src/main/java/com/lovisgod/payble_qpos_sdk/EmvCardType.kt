package com.lovisgod.payble_qpos_sdk

import com.lovisgod.kozen_p.PaybleConstants.EMV_CARD_AMEX
import com.lovisgod.kozen_p.PaybleConstants.EMV_CARD_DISCOVER
import com.lovisgod.kozen_p.PaybleConstants.EMV_CARD_INTERAC
import com.lovisgod.kozen_p.PaybleConstants.EMV_CARD_MASTERCARD
import com.lovisgod.kozen_p.PaybleConstants.EMV_CARD_MIR
import com.lovisgod.kozen_p.PaybleConstants.EMV_CARD_NOT
import com.lovisgod.kozen_p.PaybleConstants.EMV_CARD_RUPAY
import com.lovisgod.kozen_p.PaybleConstants.EMV_CARD_UNIONPAY
import com.lovisgod.kozen_p.PaybleConstants.EMV_CARD_VISA

enum class EmvCardType(val type: Int,   val schemeName: String) {
    DEFAULT(EMV_CARD_NOT, ""),
    VERVE(EMV_CARD_NOT, "VERVE"),
    AFRIGO(EMV_CARD_NOT, "AFRIGO"),
    VISA(EMV_CARD_VISA, "VISA"),
    UNIONPAY(EMV_CARD_UNIONPAY, "UnionPay"),
    MASTERCARD(EMV_CARD_MASTERCARD, "MasterCard"),
    AMERICAN_EXPRESS(EMV_CARD_AMEX, "American express"),
    DISCOVER(EMV_CARD_DISCOVER, "Discover"),
    MIR(EMV_CARD_MIR, "Mir"),
    RUPAY(EMV_CARD_RUPAY, "RuPay"),
    INTERAC(EMV_CARD_INTERAC, "Interac");

    companion object {
        fun getCardType(name: String): Int {
            var ret = DEFAULT.type
            for (`val` in entries) {
                if (`val`.name == name) {
                    ret = `val`.type
                    break
                }
            }
            return ret
        }

        fun getCardType(type: Int): String {
            var ret = DEFAULT.name
            for (`val` in entries) {
                if (`val`.type == type) {
                    ret = `val`.name
                    break
                }
            }
            return ret
        }

        fun getCardTypeX(type: Int): EmvCardType {
            var ret = DEFAULT
            for (`val` in entries) {
                if (`val`.type == type) {
                    ret = `val`
                    break
                }
            }
            return ret
        }
    }
}
