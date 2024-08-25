package com.lovisgod.payble_qpos_sdk.network.models

import com.google.gson.annotations.SerializedName

data class TerminalKeyResponse(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: TerminalKeyData
)

data class TerminalKeyData(
    @SerializedName("sessionKey") val sessionKey: String,
    @SerializedName("masterKey") val masterKey: String,
    @SerializedName("pinKey") val pinKey: String,
    @SerializedName("params") val params: TerminalParams
)

data class TerminalParams(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: TerminalData
)

data class TerminalData(
    @SerializedName("id") val id: String,
    @SerializedName("terminalCode") val terminalCode: String,
    @SerializedName("cardAcceptorId") val cardAcceptorId: String,
    @SerializedName("merchantId") val merchantId: String,
    @SerializedName("merchantName") val merchantName: String,
    @SerializedName("merchantAddress1") val merchantAddress1: String,
    @SerializedName("merchantAddress2") val merchantAddress2: String,
    @SerializedName("merchantPhoneNumber") val merchantPhoneNumber: String,
    @SerializedName("merchantEmail") val merchantEmail: String,
    @SerializedName("merchantState") val merchantState: String,
    @SerializedName("tmsRouteType") val tmsRouteType: String,
    @SerializedName("merchantCity") val merchantCity: String,
    @SerializedName("qtbMerchantCode") val qtbMerchantCode: String,
    @SerializedName("qtbMerchantAlias") val qtbMerchantAlias: String,
    @SerializedName("cardAcceptorNameLocation") val cardAcceptorNameLocation: String,
    @SerializedName("merchantCategoryCode") val merchantCategoryCode: String,
    @SerializedName("terminalCountryCode") val terminalCountryCode: String,
    @SerializedName("transCurrencyCode") val transCurrencyCode: String,
    @SerializedName("transCurrencyExp") val transCurrencyExp: String,
    @SerializedName("terminalType") val terminalType: String,
    @SerializedName("terminalCapabilities") val terminalCapabilities: String,
    @SerializedName("terminalExtCapabilities") val terminalExtCapabilities: String,
    @SerializedName("terminalEntryMode") val terminalEntryMode: String,
    @SerializedName("nibbsKey") val nibbsKey: String,
    @SerializedName("upkey") val upkey: String
)
