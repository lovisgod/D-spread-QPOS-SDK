package com.lovisgod.payble_qpos_sdk.network.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.util.*

data class AgentData(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: Details
)

data class Details(
    @SerializedName("id") val id: Int,
    @SerializedName("merchant_id") val merchantId: String,
    @SerializedName("agent_id") val agentId: String,
    @SerializedName("name") val name: String,
    @SerializedName("agent_code") val agentCode: String,
    @SerializedName("nuban") val nuban: String?,
    @SerializedName("nuban_code") val nubanCode: String?,
    @SerializedName("address") val address: String,
    @SerializedName("telephone") val telephone: String,
    @SerializedName("email") val email: String,
    @SerializedName("is_verified") val isVerified: Boolean,
    @SerializedName("remove_withdrawal_charge") val removeWithdrawalCharge: Boolean,
    @SerializedName("changed_password") val changedPassword: Boolean,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("payref") val payref: String?,
    @SerializedName("agent_transtype") val agentTranstype: String,
    @SerializedName("business_type") val businessType: String,
    @SerializedName("id_link") val idLink: String?,
    @SerializedName("bvn_number") val bvnNumber: String,
    @SerializedName("id_type") val idType: String?,
    @SerializedName("selfie_link") val selfieLink: String?,
    @SerializedName("is_sub_agent") val isSubAgent: Boolean,
    @SerializedName("owner_id") val ownerId: String?,
    @SerializedName("dob") val dob: String?,
    @SerializedName("extra") val extra: Extra,
    @SerializedName("wallet") val wallet: Wallet,
    @SerializedName("terminals") val terminals: Terminal,
    @SerializedName("agentCharge") val agentCharge: String
)

data class Extra(
    @SerializedName("wallet_balance") val walletBalance: Any,
    @SerializedName("transaction_volume") val transactionVolume: Any,
    @SerializedName("total_terminals") val totalTerminals: Any
)

data class Wallet(
    @SerializedName("id") val id: Int,
    @SerializedName("account_number") val accountNumber: String,
    @SerializedName("agent_id") val agentId: String,
    @SerializedName("balance") val balance: Any,
    @SerializedName("ledger_balance") val ledgerBalance: Any,
    @SerializedName("currency") val currency: String,
    @SerializedName("min_balance") val minBalance: Any,
    @SerializedName("max_balance") val maxBalance: Any,
    @SerializedName("provider") val provider: String,
    @SerializedName("walletcustomerid") val walletCustomerId: String,
    @SerializedName("bank_name") val bankName: String,
    @SerializedName("wallettype") val walletType: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)

data class Terminal(
    @SerializedName("id") val id: Int,
    @SerializedName("merchant_id") val merchantId: String,
    @SerializedName("agent_id") val agentId: String,
    @SerializedName("terminal_id") val terminalId: String,
    @SerializedName("upstream_serial_id") val upstreamSerialId: String,
    @SerializedName("device_serial_id") val deviceSerialId: String,
    @SerializedName("make") val make: String,
    @SerializedName("route") val route: String,
    @SerializedName("allowed_transactions") val allowedTransactions: String,
    @SerializedName("assigned") val assigned: Boolean,
    @SerializedName("active") val active: Boolean,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)

fun Any.toJson(): String {
    val gson = Gson()
    return gson.toJson(this)
}
