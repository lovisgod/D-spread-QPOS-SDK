package com.lovisgod.payble_qpos_sdk.utils;

enum class ICCData(val tagName: String, val tag: String, val min: Int, val max: Int) {
    AUTHORIZATION_REQUEST("Authorization Request", "9F26", 8, 8),
    CRYPTOGRAM_INFO_DATA("Cryptogram Information Data", "9F27", 1, 1),
    ISSUER_APP_DATA("Issuer Application Data", "9F10", 0, 32),
    UNPREDICTABLE_NUMBER("Unpredictable Number", "9F37", 4, 4),
    APPLICATION_TRANSACTION_COUNTER("App Transaction Counter", "9F36", 2, 2),
    TERMINAL_VERIFICATION_RESULT("Terminal Verification Result", "95", 5, 5),
    TRANSACTION_DATE("Transaction Date", "9A", 3, 3),
    TRANSACTION_TYPE("Transaction Type", "9C", 1, 1),
    TRANSACTION_AMOUNT("Transaction Amount", "9F02", 6, 6),
    TRANSACTION_CURRENCY_CODE("Currency Code", "5F2A", 2, 2),
    APPLICATION_INTERCHANGE_PROFILE("App Interchange Profile", "82", 2, 2),
    TERMINAL_COUNTRY_CODE("Terminal Country Code", "9F1A", 2, 2),
    CARD_HOLDER_VERIFICATION_RESULT("CVM Results", "9F34", 3, 3),
    TERMINAL_CAPABILITIES("Terminal Capabilities", "9F33", 3, 3),
    TERMINAL_TYPE("Terminal Type", "9F35", 1, 1),
    INTERFACE_DEVICE_SERIAL_NUMBER("IDSN", "9F1E", 8, 8),
    DEDICATED_FILE_NAME("Dedicated File Name", "84", 5, 16),
    APP_VERSION_NUMBER("App Version Number", "9F09", 2, 2),
    ANOTHER_AMOUNT("Amount", "9F03", 6, 6),
    APP_PAN_SEQUENCE_NUMBER("App PAN Sequence Number", "5F34", 1, 1),
    FORM_FACTOR_INDICATOR("Form Factor Indicator", "9F6E", 1, 4),
    CARD_HOLDER_NAME("Card Holder Name", "5F20", 6, 15),
    TERMINAL_ENTRY_POINT("Point-of-Service (POS) Entry Mode", "9F39", 1, 1),
    ISSUER_AUTHENTICATION("Issuer Authentication", "91", 0, 16),
    ISSUER_SCRIPT1("Issuer Script 1", "71", 0, 128),
    ISSUER_SCRIPT2("Issuer Script 2", "72", 0, 128);
}

val REQUEST_TAGS = listOf(
    ICCData.AUTHORIZATION_REQUEST,
    ICCData.CRYPTOGRAM_INFO_DATA,
    ICCData.ISSUER_APP_DATA,
    ICCData.UNPREDICTABLE_NUMBER,
    ICCData.APPLICATION_TRANSACTION_COUNTER,
    ICCData.TERMINAL_VERIFICATION_RESULT,
    ICCData.TRANSACTION_DATE,
    ICCData.TRANSACTION_TYPE,
    ICCData.TRANSACTION_AMOUNT,
    ICCData.TRANSACTION_CURRENCY_CODE,
    ICCData.APPLICATION_INTERCHANGE_PROFILE,
    ICCData.TERMINAL_COUNTRY_CODE,
    ICCData.CARD_HOLDER_VERIFICATION_RESULT,
    ICCData.TERMINAL_CAPABILITIES,
    ICCData.TERMINAL_TYPE,
    ICCData.INTERFACE_DEVICE_SERIAL_NUMBER,
    ICCData.DEDICATED_FILE_NAME,
    ICCData.APP_VERSION_NUMBER,
    ICCData.ANOTHER_AMOUNT,
    ICCData.APP_PAN_SEQUENCE_NUMBER,
    ICCData.FORM_FACTOR_INDICATOR,
    ICCData.CARD_HOLDER_NAME,
    ICCData.TERMINAL_ENTRY_POINT
)

fun buildIccString(tagValues: List<Pair<ICCData, String>>): String {
    var hex = ""

    for (tagValue in tagValues) {
        val tag = tagValue.first.tag
        var value = tagValue.second
        var length = value.length.toString(16).uppercase()

        println(tagValue.first.tagName)

        // Truncate tag value if it exceeds max length
        if (value.length > tagValue.first.max) {
            val expectedLength = tagValue.first.max
            value = value.substring(0, expectedLength)
            length = tagValue.first.max.toString(16).uppercase()
        }

        // Prepend 0 based on value length
        val lengthStr = if (length.length > 1) length else "0$length".uppercase()
        hex += "$tag$lengthStr$value"
    }

    return hex
}
