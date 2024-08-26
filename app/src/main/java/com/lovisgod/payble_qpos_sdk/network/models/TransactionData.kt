package com.lovisgod.payble_qpos_sdk.network.models

data class TransactionData(
    val merchantCategoryCode: String,
    val terminalCode: String,
    val merchantName: String,
    val merchantId: String,
    val haspin: Boolean,
    val track2Data: String,
    val panSequenceNumber: String,
    val cardHolderName: String,
    val cardType: String,
    val cardExpiry: String,
    val amount: String,
    val pinBlock: String,
    val posDataCode: String,
    val iccString: String,
    val items: List<Item>? = null
)

data class Item(
    val _id: String,
    val name: String,
    val color: String,
    val sellingPrice: String,
    val barCode: String,
    val SKU: String,
    val image: String,
    val currency: String,
    val quantity: String
)
