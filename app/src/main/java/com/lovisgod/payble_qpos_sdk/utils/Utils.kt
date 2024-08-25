package com.lovisgod.payble_qpos_sdk.utils

import com.lovisgod.kozen_p.EventConstant
import com.lovisgod.kozen_p.PaybleConstants
import com.lovisgod.kozen_p.PaybleConstants.ANOTHER_AMOUNT
import com.lovisgod.kozen_p.PaybleConstants.APPLICATION_INTERCHANGE_PROFILE
import com.lovisgod.kozen_p.PaybleConstants.APPLICATION_TRANSACTION_COUNTER
import com.lovisgod.kozen_p.PaybleConstants.APP_PAN_SEQUENCE_NUMBER
import com.lovisgod.kozen_p.PaybleConstants.APP_VERSION_NUMBER
import com.lovisgod.kozen_p.PaybleConstants.AUTHORIZATION_REQUEST
import com.lovisgod.kozen_p.PaybleConstants.CARD_HOLDER_NAME
import com.lovisgod.kozen_p.PaybleConstants.CARD_HOLDER_VERIFICATION_RESULT
import com.lovisgod.kozen_p.PaybleConstants.CRYPTOGRAM_INFO_DATA
import com.lovisgod.kozen_p.PaybleConstants.DEDICATED_FILE_NAME
import com.lovisgod.kozen_p.PaybleConstants.ICC_STRING
import com.lovisgod.kozen_p.PaybleConstants.INTERFACE_DEVICE_SERIAL_NUMBER
import com.lovisgod.kozen_p.PaybleConstants.ISSUER_APP_DATA
import com.lovisgod.kozen_p.PaybleConstants.KSN
import com.lovisgod.kozen_p.PaybleConstants.PIN_BLOCK
import com.lovisgod.kozen_p.PaybleConstants.TERMINAL_CAPABILITIES
import com.lovisgod.kozen_p.PaybleConstants.TERMINAL_COUNTRY_CODE
import com.lovisgod.kozen_p.PaybleConstants.TERMINAL_TYPE
import com.lovisgod.kozen_p.PaybleConstants.TERMINAL_VERIFICATION_RESULT
import com.lovisgod.kozen_p.PaybleConstants.TRACK_2_DATA
import com.lovisgod.kozen_p.PaybleConstants.TRANSACTION_AMOUNT
import com.lovisgod.kozen_p.PaybleConstants.TRANSACTION_CURRENCY_CODE
import com.lovisgod.kozen_p.PaybleConstants.TRANSACTION_DATE
import com.lovisgod.kozen_p.PaybleConstants.TRANSACTION_TYPE
import com.lovisgod.kozen_p.PaybleConstants.UNPREDICTABLE_NUMBER
import java.util.Hashtable

object utils {

    fun convertTransResult(tlvHelper: TLVHelper, amount: String) {
        val event: MutableMap<String, Any> = HashMap()
        event[TRANSACTION_AMOUNT] = PaybleConstants.transAmount
        event[ANOTHER_AMOUNT] = ""
        event[APPLICATION_INTERCHANGE_PROFILE] = ""
        event[APPLICATION_TRANSACTION_COUNTER] = ""
        event[AUTHORIZATION_REQUEST] = ""
        event[CRYPTOGRAM_INFO_DATA] = ""
        event[CARD_HOLDER_NAME] = ""
        event[CARD_HOLDER_VERIFICATION_RESULT] = ""
        event[ISSUER_APP_DATA] = ""
        event[TRANSACTION_CURRENCY_CODE] = ""
        event[TERMINAL_TYPE] = ""
        event[TERMINAL_CAPABILITIES] = ""
        event[TERMINAL_COUNTRY_CODE] = ""
        event[TERMINAL_VERIFICATION_RESULT] = ""
        event[TRANSACTION_DATE] = ""
        event[TRANSACTION_TYPE] = ""
        event[UNPREDICTABLE_NUMBER] = ""
        event[DEDICATED_FILE_NAME] = ""
        event[ICC_STRING] = ""
        event[APP_VERSION_NUMBER] = ""
        event[INTERFACE_DEVICE_SERIAL_NUMBER] = ""
        event[APP_PAN_SEQUENCE_NUMBER] = ""
        event[PIN_BLOCK] = ""
        event[KSN] = ""
        event[TRACK_2_DATA] = ""
    }
}