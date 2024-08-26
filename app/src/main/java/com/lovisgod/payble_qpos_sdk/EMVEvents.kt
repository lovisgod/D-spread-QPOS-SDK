package com.lovisgod.payble_qpos_sdk

import android.bluetooth.BluetoothDevice
import com.lovisgod.kozen_p.PaybleConstants

interface EMVEvents {

    fun onInsertCard()
    fun onRemoveCard(isContactlessTransLimit: Boolean, message: String)
    fun onPinInput(): String? = ""
    fun onCardRead(pan: String, cardType: PaybleConstants.CardType)
    fun onCardDetected(contact: Boolean = true)
    fun onEmvProcessing(message: String = "Please wait while we read card")
    fun onEmvProcessed(data: Any)
    fun onUserCanceled(message: String = "User interrupted the transaction")
    fun onTransactionCancelled(message: String = "Transaction cancelled")
    fun offlinePinTryExceeded()
    fun onPinInputText(text: String)
    fun onMessage(message: String)
    fun nodeviceDetected(message: String = "No device detected")
    fun onDeviceConnected(message: String = "Device connected", deviceName : String )
    fun onDeviceDisconnected(message: String = "Device disconnected")
    fun onDeviceConnectFailed(message: String = "Device bond failed")
    fun onDeviceBondSucceed(message: String = "Device Bond Succeed")
    fun onDeviceBonding(message: String = "Device Bonding")
    fun onDeviceFound(device: MutableMap<String, Any>)
    fun onDeviceNotFound()
    fun onAgentDetailsDownloading()
    fun onAgentDetailsDownloaded()
    fun onAgentDetailsDownloadError(message: String)

    fun onAgentTransactionOnline()
    fun onAgentTransactionOnlineResponse(response: Any)
    fun onAgentTransactionError(message: String)
}