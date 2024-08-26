package com.lovisgod.payble_qpos_sdk.qpos_mini

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import com.dspread.print.util.TRACE
import com.dspread.xpos.CQPOSService
import com.dspread.xpos.QPOSService
import com.dspread.xpos.QPOSService.DoTradeResult
import com.dspread.xpos.QPOSService.TransactionResult
import com.dspread.xpos.QPOSService.UpdateInformationResult
import com.dspread.xpos.Util
import com.dspread.xpos.Util.HexStringToByteArray
import com.dspread.xpos.utils.AESUtil
import com.lovisgod.kozen_p.EventConstant
import com.lovisgod.kozen_p.PaybleConstants
import com.lovisgod.kozen_p.PaybleConstants.transactionType
import com.lovisgod.kozen_p.PaybleConstants.transactionTypeString
import com.lovisgod.payble_qpos_sdk.CardTypeUtils
import com.lovisgod.payble_qpos_sdk.EMVEvents
import com.lovisgod.payble_qpos_sdk.network.RetrofitInstance
import com.lovisgod.payble_qpos_sdk.network.models.AgentData
import com.lovisgod.payble_qpos_sdk.network.models.TerminalKeyResponse
import com.lovisgod.payble_qpos_sdk.network.models.TransactionData
import com.lovisgod.payble_qpos_sdk.network.models.fromJson
import com.lovisgod.payble_qpos_sdk.utils.EncryptedPrefsHelper
import com.lovisgod.payble_qpos_sdk.utils.FileUtils
import com.lovisgod.payble_qpos_sdk.utils.QPOSConversionUtil
import com.lovisgod.payble_qpos_sdk.utils.TLVHelper
import com.lovisgod.payble_qpos_sdk.utils.UtilsQpos.getKeyIndex
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Hashtable


class QposListenerHelper(val pos: QPOSService, val emvEvents: EMVEvents, val context: Context) : CQPOSService() {
    override fun onDoTradeResult(result: DoTradeResult, decodeData: Hashtable<String, String>?) {
        var cardNo: String? = ""
        var msg: String? = ""
        if (result == DoTradeResult.NONE) {
            msg = EventConstant.NO_CARD_DETECTED
            Log.w("paymentActivity", "msg==$msg")
            emvEvents.onTransactionCancelled(message = "No card detected")
        } else if (result == DoTradeResult.TRY_ANOTHER_INTERFACE) {
            msg = EventConstant.TRY_ANOTHER_INTERFACE
            Log.w("paymentActivity", "msg==$msg")
            emvEvents.onTransactionCancelled(message = "Try another interface")
        } else if (result == DoTradeResult.ICC) {
            msg = EventConstant.ICC_CARD_DETECTED
            emvEvents.onCardDetected(contact = true)
            pos.doEmvApp(QPOSService.EmvOption.START)
        } else if (result == DoTradeResult.NOT_ICC) {
//                statusEditText.setText(getString(R.string.card_inserted));
            msg = EventConstant.CARD_DETECTED
            Log.w("paymentActivity", "msg==$msg")
        } else if (result == DoTradeResult.BAD_SWIPE) {

            msg = EventConstant.BAD_SWIPE
            Log.w("paymentActivity", "msg==$msg")
        } else if (result == DoTradeResult.CARD_NOT_SUPPORT) {
            msg = EventConstant.GPO_NOT_SUPPORTED
            Log.w("paymentActivity", "msg==$msg")
        } else if (result == DoTradeResult.PLS_SEE_PHONE) {
            msg = EventConstant.PLEASE_SEE_PHONE
            Log.w("paymentActivity", "msg==$msg")
        } else if (result == DoTradeResult.MCR) { //Magnetic card
            msg = EventConstant.CARD_SWIPED
            if (decodeData !=null) {
                val formatID = decodeData["formatID"]
                if (formatID == "31" || formatID == "40" || formatID == "37" || formatID == "17" || formatID == "11" || formatID == "10") {
                    val maskedPAN = decodeData["maskedPAN"]
                    val expiryDate = decodeData["expiryDate"]
                    val cardHolderName = decodeData["cardholderName"]
                    val serviceCode = decodeData["serviceCode"]
                    val trackblock = decodeData["trackblock"]
                    val psamId = decodeData["psamId"]
                    val posId = decodeData["posId"]
                    val pinblock = decodeData["pinblock"]
                    val macblock = decodeData["macblock"]
                    val activateCode = decodeData["activateCode"]
                    val trackRandomNumber = decodeData["trackRandomNumber"]
                    cardNo = maskedPAN
                } else if (formatID == "FF") {
                    val type = decodeData["type"]
                    val encTrack1 = decodeData["encTrack1"]
                    val encTrack2 = decodeData["encTrack2"]
                    val encTrack3 = decodeData["encTrack3"]
                } else {
                    val orderID = decodeData["orderId"]
                    val maskedPAN = decodeData["maskedPAN"]
                    val expiryDate = decodeData["expiryDate"]
                    val cardHolderName = decodeData["cardholderName"]
                    //					String ksn = decodeData.get("ksn");
                    val serviceCode = decodeData["serviceCode"]
                    val track1Length = decodeData["track1Length"]
                    val track2Length = decodeData["track2Length"]
                    val track3Length = decodeData["track3Length"]
                    val encTracks = decodeData["encTracks"]
                    val encTrack1 = decodeData["encTrack1"]
                    val encTrack2 = decodeData["encTrack2"]
                    val encTrack3 = decodeData["encTrack3"]
                    val partialTrack = decodeData["partialTrack"]
                    val pinKsn = decodeData["pinKsn"]
                    val trackksn = decodeData["trackksn"]
                    val pinBlock = decodeData["pinBlock"]
                    val encPAN = decodeData["encPAN"]
                    val trackRandomNumber = decodeData["trackRandomNumber"]
                    val pinRandomNumber = decodeData["pinRandomNumber"]

                    cardNo = maskedPAN
            }
            }
           // handle this part for magnetic transactions
        } else if (result == DoTradeResult.NFC_ONLINE || result == DoTradeResult.NFC_OFFLINE) {

            msg = EventConstant.CARD_TAPPED
            if (decodeData != null) {
                val formatID = decodeData["formatID"]
                if (formatID == "31" || formatID == "40" || formatID == "37" || formatID == "17" || formatID == "11" || formatID == "10") {
                    val maskedPAN = decodeData["maskedPAN"]
                    val expiryDate = decodeData["expiryDate"]
                    val cardHolderName = decodeData["cardholderName"]
                    val serviceCode = decodeData["serviceCode"]
                    val trackblock = decodeData["trackblock"]
                    val psamId = decodeData["psamId"]
                    val posId = decodeData["posId"]
                    val pinblock = decodeData["pinblock"]
                    val macblock = decodeData["macblock"]
                    val activateCode = decodeData["activateCode"]
                    val trackRandomNumber = decodeData["trackRandomNumber"]
                    cardNo = maskedPAN
                } else {
                    val maskedPAN = decodeData["maskedPAN"]
                    val expiryDate = decodeData["expiryDate"]
                    val cardHolderName = decodeData["cardholderName"]
                    val serviceCode = decodeData["serviceCode"]
                    val track1Length = decodeData["track1Length"]
                    val track2Length = decodeData["track2Length"]
                    val track3Length = decodeData["track3Length"]
                    val encTracks = decodeData["encTracks"]
                    val encTrack1 = decodeData["encTrack1"]
                    val encTrack2 = decodeData["encTrack2"]
                    val encTrack3 = decodeData["encTrack3"]
                    val partialTrack = decodeData["partialTrack"]
                    val pinKsn = decodeData["pinKsn"]
                    val trackksn = decodeData["trackksn"]
                    val pinBlock = decodeData["pinBlock"]
                    val encPAN = decodeData["encPAN"]
                    val trackRandomNumber = decodeData["trackRandomNumber"]
                    val pinRandomNumber = decodeData["pinRandomNumber"]
                    cardNo = maskedPAN
                }
            }

        } else if (result == DoTradeResult.NFC_DECLINED) {

            msg = EventConstant.TRANSACTION_CANCEL
            Log.w("paymentActivity", "msg==$msg")
            emvEvents.onTransactionCancelled(message = "Nfd transaction declined")
        } else if (result == DoTradeResult.NO_RESPONSE) {
            msg = EventConstant.CARD_NO_RESPONSE
            Log.w("paymentActivity", "msg==$msg")
            emvEvents.onTransactionCancelled(message = "Card no response")
        } else {
            msg = EventConstant.TRANSACTION_CANCEL
            Log.w("paymentActivity", "msg==$msg")
            emvEvents.onTransactionCancelled()
        }
        // return message here if neccesary
    }

    override fun onQposInfoResult(posInfoData: Hashtable<String, String>) {
//        tvTitle.setText(getString(R.string.get_info))
//        com.dspread.demoui.activity.PaymentActivity.dismissDialog()
        TRACE.d("onQposInfoResult$posInfoData")
        val isSupportedTrack1 =
            if (posInfoData["isSupportedTrack1"] == null) "" else posInfoData["isSupportedTrack1"]!!
        val isSupportedTrack2 =
            if (posInfoData["isSupportedTrack2"] == null) "" else posInfoData["isSupportedTrack2"]!!
        val isSupportedTrack3 =
            if (posInfoData["isSupportedTrack3"] == null) "" else posInfoData["isSupportedTrack3"]!!
        val bootloaderVersion =
            if (posInfoData["bootloaderVersion"] == null) "" else posInfoData["bootloaderVersion"]!!
        val firmwareVersion =
            if (posInfoData["firmwareVersion"] == null) "" else posInfoData["firmwareVersion"]!!
        val isUsbConnected =
            if (posInfoData["isUsbConnected"] == null) "" else posInfoData["isUsbConnected"]!!
        val isCharging = if (posInfoData["isCharging"] == null) "" else posInfoData["isCharging"]!!
        val batteryLevel =
            if (posInfoData["batteryLevel"] == null) "" else posInfoData["batteryLevel"]!!
        val batteryPercentage =
            if (posInfoData["batteryPercentage"] == null) "" else posInfoData["batteryPercentage"]!!
        val hardwareVersion =
            if (posInfoData["hardwareVersion"] == null) "" else posInfoData["hardwareVersion"]!!
        val SUB = if (posInfoData["SUB"] == null) "" else posInfoData["SUB"]!!
        val pciFirmwareVersion =
            if (posInfoData["PCI_firmwareVersion"] == null) "" else posInfoData["PCI_firmwareVersion"]!!
        val pciHardwareVersion =
            if (posInfoData["PCI_hardwareVersion"] == null) "" else posInfoData["PCI_hardwareVersion"]!!
        val compileTime =
            if (posInfoData["compileTime"] == null) "" else posInfoData["compileTime"]!!
//        var content = ""
//        content += getString(R.string.bootloader_version) + bootloaderVersion + "\n"
//        content += getString(R.string.firmware_version) + firmwareVersion + "\n"
//        content += getString(R.string.usb) + isUsbConnected + "\n"
//        content += getString(R.string.charge) + isCharging + "\n"
//        //			if (batteryPercentage==null || "".equals(batteryPercentage)) {
//        content += getString(R.string.battery_level) + batteryLevel + "\n"
//        //			}else {
//        content += getString(R.string.battery_percentage) + batteryPercentage + "\n"
//        //			}
//        content += getString(R.string.hardware_version) + hardwareVersion + "\n"
//        content += "SUB : $SUB\n"
//        content += getString(R.string.track_1_supported) + isSupportedTrack1 + "\n"
//        content += getString(R.string.track_2_supported) + isSupportedTrack2 + "\n"
//        content += getString(R.string.track_3_supported) + isSupportedTrack3 + "\n"
//        content += "PCI FirmwareVresion:$pciFirmwareVersion\n"
//        content += "PCI HardwareVersion:$pciHardwareVersion\n"
//        content += "compileTime:$compileTime\n"
//        mllinfo.setVisibility(View.VISIBLE)
//        tradeSuccess.setVisibility(View.GONE)
//        mbtnNewpay.setVisibility(View.GONE)
//        mtvinfo.setText(content)
//        mllchrccard.setVisibility(View.GONE)
    }

    /**
     * @see QPOSService.QPOSServiceListener.onRequestTransactionResult
     */
    override fun onRequestTransactionResult(transactionResult: TransactionResult) {
        TRACE.d("onRequestTransactionResult()$transactionResult")
        if (transactionResult == TransactionResult.CARD_REMOVED) {
        }

        var msg = ""
        if (transactionResult == TransactionResult.APPROVED) {
            TRACE.d("TransactionResult.APPROVED")

        } else if (transactionResult == TransactionResult.TERMINATED) {
            msg = EventConstant.TRANSACTION_TERMINATED
        } else if (transactionResult == TransactionResult.DECLINED) {
            msg = EventConstant.TRANSACTION_DENIED
        } else if (transactionResult == TransactionResult.CANCEL) {
            msg = EventConstant.TRANSACTION_CANCEL
        } else if (transactionResult == TransactionResult.CAPK_FAIL) {
            msg = EventConstant.CAPK_FAILED
        } else if (transactionResult == TransactionResult.NOT_ICC) {
            msg = EventConstant.NOT_ICC
        } else if (transactionResult == TransactionResult.SELECT_APP_FAIL) {
            msg = "SELECT APP FAILED"
        } else if (transactionResult == TransactionResult.DEVICE_ERROR) {
            msg = EventConstant.DEVICE_ERROR
        } else if (transactionResult == TransactionResult.TRADE_LOG_FULL) {
            msg = "the trade log has fulled!pls clear the trade log!"
        } else if (transactionResult == TransactionResult.CARD_NOT_SUPPORTED) {
            msg = EventConstant.CARD_NOT_SUPPORTED
        } else if (transactionResult == TransactionResult.MISSING_MANDATORY_DATA) {
            msg = "MISSING MANDATORY DATA"
        } else if (transactionResult == TransactionResult.CARD_BLOCKED_OR_NO_EMV_APPS) {
            msg = EventConstant.CARD_BLOCKED_OR_NO_EMVDATA
        } else if (transactionResult == TransactionResult.INVALID_ICC_DATA) {
            msg = EventConstant.INVALID_ICC_DATA
        } else if (transactionResult == TransactionResult.FALLBACK) {
            msg = EventConstant.TRANS_FALLBACK
        } else if (transactionResult == TransactionResult.NFC_TERMINATED) {
            msg = "NFC Terminated"
        } else if (transactionResult == TransactionResult.CARD_REMOVED) {
            msg = EventConstant.TRANSACTION_CANCELED_REMOVE_CARD
        } else if (transactionResult == TransactionResult.CONTACTLESS_TRANSACTION_NOT_ALLOW) {
            msg = "TRANS NOT ALLOW"
        } else if (transactionResult == TransactionResult.CARD_BLOCKED) {
            msg = EventConstant.CARD_BLOCKED_OR_NO_EMVDATA
        } else if (transactionResult == TransactionResult.TRANS_TOKEN_INVALID) {
            msg = "TOKEN INVALID"
        } else if (transactionResult == TransactionResult.APP_BLOCKED) {
            msg = "APP BLOCKED"
        } else {
            msg = transactionResult.name
        }
        Log.w("TAG", "transactionResult==$msg")
        Log.w("transactionResult", "transactionResult==$transactionResult")
    }

    override fun onRequestBatchData(tlv: String) {
        TRACE.d("ICC trade finished")
        var content = tlv
    }

    override fun onRequestTransactionLog(tlv: String) {
        TRACE.d("onRequestTransactionLog(String tlv):$tlv")
    }

    override fun onQposIdResult(posIdTable: Hashtable<String, String>) {
        TRACE.w("onQposIdResult():$posIdTable")
        val posId = if (posIdTable["posId"] == null) "" else posIdTable["posId"]!!
        val csn = if (posIdTable["csn"] == null) "" else posIdTable["csn"]!!
        val psamId = if (posIdTable["psamId"] == null) "" else posIdTable["psamId"]!!
        val NFCId = if (posIdTable["nfcID"] == null) "" else posIdTable["nfcID"]!!
        var content = ""
        content += "csn: $csn\n"
        content += ("conn: " + pos.getBluetoothState()).toString() + "\n"
        content += "psamId: $psamId\n"
        content += "NFCId: $NFCId\n"
    }

    override fun onRequestSelectEmvApp(appList: ArrayList<String>) {
        TRACE.d("onRequestSelectEmvApp():$appList")
        TRACE.d("Please select App -- S，emv card config")

        val appNameList = arrayOfNulls<String>(appList.size)
        for (i in appNameList.indices) {
            appNameList[i] = appList[i]
        }
//        appListView =
//            com.dspread.demoui.activity.PaymentActivity.dialog.findViewById<View>(R.id.appList) as ListView
//        appListView.setAdapter(
//            ArrayAdapter<String?>(
//                this@PaymentActivity,
//                R.layout.simple_list_item_1,
//                appNameList
//            )
//        )
//        appListView.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            pos.selectEmvApp(0)
            TRACE.d("select emv app position = $0")
//            com.dspread.demoui.activity.PaymentActivity.dismissDialog()
//        })
    }

    override fun onRequestWaitingUser() { //wait user to insert/swipe/tap card
        TRACE.d("onRequestWaitingUser()")
        emvEvents.onInsertCard()
    }

    override fun onQposRequestPinResult(dataList: List<String>, offlineTime: Int) {
        super.onQposRequestPinResult(dataList, offlineTime)
        val onlinePin: Boolean = pos.isOnlinePin()
        if (onlinePin) {

        } else {
            val cvmPinTryLimit: Int = pos.getCvmPinTryLimit()
            TRACE.d("PinTryLimit:$cvmPinTryLimit")
            if (cvmPinTryLimit == 1) {

            } else {
               emvEvents.offlinePinTryExceeded()
            }
        }
//        com.dspread.demoui.activity.PaymentActivity.dismissDialog()
//        mllchrccard.setVisibility(View.GONE)
//        keyBoardList = dataList
//        MyKeyboardView.setKeyBoardListener(object : KeyBoardNumInterface() {
//            fun getNumberValue(value: String?) {
////                    statusEditText.setText("Pls click "+dataList.get(0));
//                pos.pinMapSync(value, 20)
//            }
//        })
//        pinpadEditText.setVisibility(View.VISIBLE)
//        keyboardUtil = KeyboardUtil(this@PaymentActivity, scvText, dataList)
//        keyboardUtil.initKeyboard(
//            MyKeyboardView.KEYBOARDTYPE_Only_Num_Pwd,
//            pinpadEditText
//        ) //Random keyboard
    }

    override fun onReturnGetKeyBoardInputResult(result: String) {
        super.onReturnGetKeyBoardInputResult(result)
        Log.w("checkUactivity", "onReturnGetKeyBoardInputResult")
    }

    override fun onReturnGetPinInputResult(num: Int) {
        super.onReturnGetPinInputResult(num)
        var s = ""
        if (num == -1) {
        } else {
            for (i in 0 until num) {
                s += "*"
            }
            emvEvents.onPinInputText(s)
        }
    }

    override fun onRequestSetAmount() {
        TRACE.d("input amount -- S")
        TRACE.d("onRequestSetAmount()")
        if (transactionTypeString != null) {
            if (transactionTypeString == "GOODS") {
                transactionType = QPOSService.TransactionType.GOODS
            } else if (transactionTypeString == "SERVICES") {
                transactionType = QPOSService.TransactionType.SERVICES
            } else if (transactionTypeString == "CASH") {
                transactionType = QPOSService.TransactionType.CASH
            } else if (transactionTypeString == "CASHBACK") {
                transactionType = QPOSService.TransactionType.CASHBACK
            } else if (transactionTypeString == "PURCHASE_REFUND") {
                transactionType = QPOSService.TransactionType.REFUND
            } else if (transactionTypeString == "INQUIRY") {
                transactionType = QPOSService.TransactionType.INQUIRY
            } else if (transactionTypeString == "TRANSFER") {
                transactionType = QPOSService.TransactionType.TRANSFER
            } else if (transactionTypeString == "ADMIN") {
                transactionType = QPOSService.TransactionType.ADMIN
            } else if (transactionTypeString == "CASHDEPOSIT") {
                transactionType = QPOSService.TransactionType.CASHDEPOSIT
            } else if (transactionTypeString == "PAYMENT") {
                transactionType = QPOSService.TransactionType.PAYMENT
            } else if (transactionTypeString == "PBOCLOG||ECQ_INQUIRE_LOG") {
                transactionType = QPOSService.TransactionType.PBOCLOG
            } else if (transactionTypeString == "SALE") {
                transactionType = QPOSService.TransactionType.SALE
            } else if (transactionTypeString == "PREAUTH") {
                transactionType = QPOSService.TransactionType.PREAUTH
            } else if (transactionTypeString == "ECQ_DESIGNATED_LOAD") {
                transactionType = QPOSService.TransactionType.ECQ_DESIGNATED_LOAD
            } else if (transactionTypeString == "ECQ_UNDESIGNATED_LOAD") {
                transactionType = QPOSService.TransactionType.ECQ_UNDESIGNATED_LOAD
            } else if (transactionTypeString == "ECQ_CASH_LOAD") {
                transactionType = QPOSService.TransactionType.ECQ_CASH_LOAD
            } else if (transactionTypeString == "ECQ_CASH_LOAD_VOID") {
                transactionType = QPOSService.TransactionType.ECQ_CASH_LOAD_VOID
            } else if (transactionTypeString == "CHANGE_PIN") {
                transactionType = QPOSService.TransactionType.UPDATE_PIN
            } else if (transactionTypeString == "REFOUND") {
                transactionType = QPOSService.TransactionType.REFUND
            } else if (transactionTypeString == "SALES_NEW") {
                transactionType = QPOSService.TransactionType.SALES_NEW
            }
            pos.setAmount(PaybleConstants.transAmount, "", PaybleConstants.currencyCode, transactionType)
        }
    }

    /**
     * @see QPOSService.QPOSServiceListener.onRequestIsServerConnected
     */
    override fun onRequestIsServerConnected() {
        TRACE.d("onRequestIsServerConnected()")
        pos.isServerConnected(true)
    }

    override fun onRequestOnlineProcess(tlv: String) {
        TRACE.d("onRequestOnlineProcess$tlv")
        val decodeData: Hashtable<String, String> = pos.anlysEmvIccData(tlv)
        TRACE.d("anlysEmvIccData(tlv):$decodeData")
        val tlvHelper = TLVHelper()
        tlvHelper.parseTLVData(tlv)

//        // THIS IS WHEN WE USE MK_SK_PLAIN
//        val cardTransactionData = CardTransactionData()
//        cardTransactionData.setValuesFromMap(decodeData)

        // Now you can access the values from cardTransactionData object
        println("Card Holder Name: ${tlvHelper.cardHolderName}")
        println("PIN Block: ${tlvHelper.pinBlock}")
        println("ICC Data: ${tlvHelper.iccString}")
        println("track 2 data: ${tlvHelper.track2Data}")
        println("card expiry: ${tlvHelper.cardExpiry}")
        println("card type: ${tlvHelper.cardType}")
        println("CSN: ${tlvHelper.panSequenceNumber}")

        // send transaction online here
        val sharedPreferences = EncryptedPrefsHelper(context)
        val agentData :AgentData = sharedPreferences.getString(EncryptedPrefsHelper.AGENT_DATA, "").toString().fromJson()
        val terminalData: TerminalKeyResponse = sharedPreferences.getString(EncryptedPrefsHelper.TERMINAL_DATA, "").toString().fromJson()
        val api = RetrofitInstance.api

        val call = api.makeCardTransaction(
            version = "1",
            save_trans = "1",
            api_key = PaybleConstants.api_key,
            mid = PaybleConstants.mid,
            sskey = terminalData.data.sessionKey,
            user_subject = agentData.data.telephone.replaceFirst("+234", "0"),
            data = TransactionData(
                merchantCategoryCode = terminalData.data.params.data.merchantCategoryCode,
                terminalCode = terminalData.data.params.data.terminalCode,
                merchantId = terminalData.data.params.data.merchantId,
                merchantName = terminalData.data.params.data.merchantName,
                haspin = !tlvHelper.pinBlock.isNullOrEmpty(),
                track2Data = tlvHelper.track2Data.toString().uppercase(),
                panSequenceNumber = tlvHelper.panSequenceNumber.toString(),
                cardHolderName = tlvHelper.cardHolderName.toString(),
                cardType = tlvHelper.cardType.toString(),
                cardExpiry = tlvHelper.cardExpiry.toString(),
                amount = PaybleConstants.transAmount,
                pinBlock = tlvHelper.pinBlock.toString().uppercase(),
                posDataCode = "510101511344101",
                iccString = tlvHelper.iccString.toString()
            )
          )

        emvEvents.onAgentTransactionOnline()

        call.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.isSuccessful) {
                    val transResponse = response.body()
                    if (transResponse != null) {
                        emvEvents.onAgentTransactionOnlineResponse(transResponse)
                    }
                    Log.d("get payment details", "response: $transResponse")
                } else {
                    emvEvents.onAgentTransactionError(message = "Error: ${response.code()}")
                    Log.e("get payment details", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                emvEvents.onAgentTransactionError(message = "Failure: ${t.message}")
                Log.e("get payment details", "Failure: ${t.message}")
            }
        })
        val str = "8A023030" //Currently the default value,

        pos.sendOnlineProcessResult(str) //脚本通知/55域/ICCDATA


//        if (isPinCanceled) {
//            mllchrccard.setVisibility(View.GONE)
//        } else {
//            mllchrccard.setVisibility(View.GONE)
//        }

    }

    override fun onRequestTime() {
        TRACE.d("onRequestTime")
        val terminalTime = SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().time)
        pos.sendTime(terminalTime)
        //            statusEditText.setText(getString(R.string.request_terminal_time) + " " + terminalTime);
    }

    override fun onRequestDisplay(displayMsg: QPOSService.Display) {
        TRACE.d("onRequestDisplay(Display displayMsg):$displayMsg")

        var msg: String? = ""
        if (displayMsg == QPOSService.Display.CLEAR_DISPLAY_MSG) {
            msg = ""
        } else if (displayMsg == QPOSService.Display.MSR_DATA_READY) {

        } else if (displayMsg == QPOSService.Display.PLEASE_WAIT) {
            msg = "Please wait"
        } else if (displayMsg == QPOSService.Display.REMOVE_CARD) {
            msg = "REMOVE CARD"
        } else if (displayMsg == QPOSService.Display.TRY_ANOTHER_INTERFACE) {
            msg = EventConstant.TRY_ANOTHER_INTERFACE
        } else if (displayMsg == QPOSService.Display.PROCESSING) {
            msg = "PROCESSING"
        } else if (displayMsg == QPOSService.Display.INPUT_PIN_ING) {
            msg = "please input pin on pos"
        } else if (displayMsg == QPOSService.Display.INPUT_OFFLINE_PIN_ONLY || displayMsg == QPOSService.Display.INPUT_LAST_OFFLINE_PIN) {
            msg = "please input offline pin on pos"
        } else if (displayMsg == QPOSService.Display.MAG_TO_ICC_TRADE) {
            msg = "please insert chip card on pos"
        } else if (displayMsg == QPOSService.Display.CARD_REMOVED) {
            msg = "card removed"
        } else if (displayMsg == QPOSService.Display.TRANSACTION_TERMINATED) {
            msg = "transaction terminated"
        } else if (displayMsg == QPOSService.Display.PlEASE_TAP_CARD_AGAIN) {
            msg = "PLEASE TAP CARD AGAIN"
        }
        //            Log.w("displayMsg==", "displayMsg==" + msg);
//            Toast.makeText(CheckActivity.this, msg, Toast.LENGTH_SHORT).show();
        if (msg != null) {
            emvEvents.onMessage(msg)
        }
    }

    override fun onRequestFinalConfirm() {
        TRACE.d("onRequestFinalConfirm() ")
        TRACE.d("onRequestFinalConfirm - S")
    }

    override fun onRequestNoQposDetected() {
        TRACE.d("onRequestNoQposDetected()")
        Log.w("onRequestNoQposDetected", "No pos detected.")
        emvEvents.nodeviceDetected(message = "No pos detected.")
    }

    override fun onRequestQposConnected() {
        TRACE.d("onRequestQposConnected()type==")
        val randomInt = (0..100).random()
        emvEvents.onDeviceConnected(deviceName = "BLUETOOTH DEVICE${randomInt}")

        val keyIdex: Int = getKeyIndex()

       getPosInfo("posid")

       updatePosInfo("setMasterkey")
       updatePosInfo("updateWorkkey")

//                pos.doTrade(keyIdex, 30) //start do trade
    }

    override fun onRequestQposDisconnected() {
        emvEvents.onDeviceDisconnected()
        TRACE.d("onRequestQposDisconnected()")
    }

    override fun onError(errorState: QPOSService.Error) {

        TRACE.d("onError$errorState")
        var msg: String? = ""
        if (errorState == QPOSService.Error.CMD_NOT_AVAILABLE) {
            msg = "Command not available"
        } else if (errorState == QPOSService.Error.TIMEOUT) {
            msg = "DEVICE TIMEOUT"
        } else if (errorState == QPOSService.Error.DEVICE_RESET) {
            msg = "DEVICE RESET"
        } else if (errorState == QPOSService.Error.UNKNOWN) {
            msg = "UNKNOWN ERROR"
        } else if (errorState == QPOSService.Error.DEVICE_BUSY) {
            msg = "DEVICE BUSY"
        } else if (errorState == QPOSService.Error.INPUT_OUT_OF_RANGE) {
            msg = "INPUT OUT OF RANGE"
        } else if (errorState == QPOSService.Error.INPUT_INVALID_FORMAT) {
            msg = "INVALID INPUT FORMAT"
        } else if (errorState == QPOSService.Error.INPUT_ZERO_VALUES) {
            msg = "INPUT ZERO VALUES"
        } else if (errorState == QPOSService.Error.INPUT_INVALID) {
            msg = "INVALID INPUT"
        } else if (errorState == QPOSService.Error.CASHBACK_NOT_SUPPORTED) {
            msg = "CASHBACK NOT SUPPORTED"
        } else if (errorState == QPOSService.Error.CRC_ERROR) {
            msg ="CRC ERROR"
        } else if (errorState == QPOSService.Error.COMM_ERROR) {
            msg = "COM ERROR"
        } else if (errorState == QPOSService.Error.MAC_ERROR) {
            msg = "MAC ERROR"
        } else if (errorState == QPOSService.Error.APP_SELECT_TIMEOUT) {
            msg = "APP SELECT TIMEOUT"
        } else if (errorState == QPOSService.Error.CMD_TIMEOUT) {
            msg = "CMD TIMEOUT"
        } else if (errorState == QPOSService.Error.ICC_ONLINE_TIMEOUT) {
            if (pos == null) {
                return
            }
            pos.resetPosStatus()
            msg = "DEVICE RESET"
        } else {
            msg = errorState.name
        }
       emvEvents.onTransactionCancelled(msg)
    }

    override fun onReturnReversalData(tlv: String) {

    }

    override fun onReturnServerCertResult(serverSignCert: String, serverEncryptCert: String) {
        super.onReturnServerCertResult(serverSignCert, serverEncryptCert)
    }

    override fun onReturnGetPinResult(result: Hashtable<String, String>) {
        TRACE.d("onReturnGetPinResult(Hashtable<String, String> result):$result")
        val pinBlock = result["pinBlock"]
        val pinKsn = result["pinKsn"]
        var content = "get pin result\n"
        content += "$pinKsn\n"
        content += " $pinBlock\n"
        TRACE.i(content)
    }

    override fun onReturnApduResult(arg0: Boolean, arg1: String, arg2: Int) {
        TRACE.d(("onReturnApduResult(boolean arg0, String arg1, int arg2):$arg0\n").toString() + arg1 + "\n" + arg2)
    }

    override fun onReturnPowerOffIccResult(arg0: Boolean) {
        TRACE.d("onReturnPowerOffIccResult(boolean arg0):$arg0")
    }

    override fun onReturnPowerOnIccResult(arg0: Boolean, arg1: String, arg2: String, arg3: Int) {
        TRACE.d((("onReturnPowerOnIccResult(boolean arg0, String arg1, String arg2, int arg3) :$arg0\n").toString() + arg1 + "\n").toString() + arg2 + "\n" + arg3)
        if (arg0) {
            pos.sendApdu("123456")
        }
    }

    override fun onReturnSetSleepTimeResult(isSuccess: Boolean) {
        TRACE.d("onReturnSetSleepTimeResult(boolean isSuccess):$isSuccess")
        var content = ""
        content = if (isSuccess) {
            "set the sleep time success."
        } else {
            "set the sleep time failed."
        }
    }

    override fun onGetCardNoResult(cardNo: String) {
        TRACE.d("onGetCardNoResult(String cardNo):$cardNo")
        emvEvents.onCardRead(pan = cardNo, cardType = CardTypeUtils.getCardType(cardNo))
    }

    override fun onRequestCalculateMac(calMac: String) {
        var calMac: String? = calMac
        TRACE.d("onRequestCalculateMac(String calMac):$calMac")
        if (calMac != null && "" != calMac) {
            calMac = QPOSConversionUtil.byteArray2Hex(calMac.toByteArray())
        }
        TRACE.d("calMac_result: calMac=> e: $calMac")
    }

    override fun onRequestSignatureResult(arg0: ByteArray) {
        TRACE.d("onRequestSignatureResult(byte[] arg0):$arg0")
    }

    override fun onRequestUpdateWorkKeyResult(result: UpdateInformationResult) {

        TRACE.d("onRequestUpdateWorkKeyResult(UpdateInformationResult result):$result")
//        if (result == UpdateInformationResult.UPDATE_SUCCESS) {
//
//        } else if (result == UpdateInformationResult.UPDATE_FAIL) {
//            mtvinfo.setText(getString(R.string.updateworkkey_fail))
//        } else if (result == UpdateInformationResult.UPDATE_PACKET_VEFIRY_ERROR) {
//            mtvinfo.setText(getString(R.string.workkey_vefiry_error))
//        } else if (result == UpdateInformationResult.UPDATE_PACKET_LEN_ERROR) {
//            mtvinfo.setText(getString(R.string.workkey_packet_Len_error))
//        }
//        mllinfo.setVisibility(View.VISIBLE)
//        mbtnNewpay.setVisibility(View.GONE)
//        mllchrccard.setVisibility(View.GONE)
//        tradeSuccess.setVisibility(View.GONE)
    }

    override fun onReturnCustomConfigResult(isSuccess: Boolean, result: String) {

    }

    override fun onRequestSetPin() {
        TRACE.i("onRequestSetPin()")
        val pinText = emvEvents.onPinInput()
        PaybleConstants.pxtxt = pinText.toString()
        val pinBlock = buildCvmPinBlock(
            pos.getEncryptData(),
            pinText.toString()
        ) // build the ISO format4 pin block
        pos.sendPin(QPOSConversionUtil.HexStringToByteArray(pinText), false)
    }

    override fun onReturnSetMasterKeyResult(isSuccess: Boolean) {

    }

    override fun onReturnBatchSendAPDUResult(batchAPDUResult: LinkedHashMap<Int, String>) {
        TRACE.d("onReturnBatchSendAPDUResult(LinkedHashMap<Integer, String> batchAPDUResult):$batchAPDUResult")
        val sb = StringBuilder()
        sb.append("APDU Responses: \n")
        for ((key, value) in batchAPDUResult) {
            sb.append("[$key]: $value\n")
        }
    }

    override fun onBluetoothBondFailed() {
        TRACE.d("onBluetoothBondFailed()")
      emvEvents.onDeviceConnectFailed(message = "Bluetooth Bond Failed")
    }

    override fun onBluetoothBondTimeout() {
        TRACE.d("onBluetoothBondTimeout()")
       emvEvents.onDeviceConnectFailed(message = "Bluetooth Bond Timeout")
    }

    override fun onBluetoothBonded() {
        TRACE.d("onBluetoothBonded()")
        emvEvents.onDeviceBondSucceed(message = "Bluetooth Bonded")
    }

    override fun onBluetoothBonding() {
        TRACE.d("onBluetoothBonding()")
        emvEvents.onDeviceBonding(message = "Bluetooth Bonding")
    }

    override fun onReturniccCashBack(result: Hashtable<String, String>) {
        TRACE.d("onReturniccCashBack(Hashtable<String, String> result):$result")
        var s = "serviceCode: " + result["serviceCode"]
        s += "\n"
        s += "trackblock: " + result["trackblock"]
    }

    override fun onLcdShowCustomDisplay(arg0: Boolean) {
        TRACE.d("onLcdShowCustomDisplay(boolean arg0):$arg0")
    }

    override fun onUpdatePosFirmwareResult(arg0: UpdateInformationResult) {

        var msg = ""
        TRACE.d("onUpdatePosFirmwareResult(UpdateInformationResult arg0):$arg0")
        //            isUpdateFw = false;
        msg = if (arg0 != UpdateInformationResult.UPDATE_SUCCESS) {
            "update firmware fail"
        } else {
            //                    mhipStatus.setText("");
            "update firmware success"
        }
        TRACE.d("onUpdatePosFirmwareResult(UpdateInformationResult arg0):$msg")
    }

    override fun onReturnDownloadRsaPublicKey(map: HashMap<String, String>) {
        TRACE.d("onReturnDownloadRsaPublicKey(HashMap<String, String> map):$map")
        if (map == null) {
            TRACE.d("MainActivity++++++++++++++map == null")
            return
        }
        val randomKeyLen = map["randomKeyLen"]
        val randomKey = map["randomKey"]
        val randomKeyCheckValueLen = map["randomKeyCheckValueLen"]
        val randomKeyCheckValue = map["randomKeyCheckValue"]
        TRACE.d("randomKey$randomKey    \n    randomKeyCheckValue$randomKeyCheckValue")
    }

    override fun onGetPosComm(mod: Int, amount: String, posid: String) {
        TRACE.d((("onGetPosComm(int mod, String amount, String posid):$mod\n").toString() + amount + "\n") + posid)
    }

    override fun onPinKey_TDES_Result(arg0: String) {
        TRACE.d("onPinKey_TDES_Result(String arg0):$arg0")
    }

    override fun onUpdateMasterKeyResult(arg0: Boolean, arg1: Hashtable<String, String>) {
        TRACE.d(("onUpdateMasterKeyResult(boolean arg0, Hashtable<String, String> arg1):$arg0\n").toString() + arg1.toString())
    }

    override fun onEmvICCExceptionData(arg0: String) {
        TRACE.d("onEmvICCExceptionData(String arg0):$arg0")
    }

    override fun onSetParamsResult(arg0: Boolean, arg1: Hashtable<String, Any>) {
        TRACE.d(("onSetParamsResult(boolean arg0, Hashtable<String, Object> arg1):$arg0\n").toString() + arg1.toString())
    }

    override fun onGetInputAmountResult(arg0: Boolean, arg1: String) {
        TRACE.d(("onGetInputAmountResult(boolean arg0, String arg1):$arg0\n").toString() + arg1.toString())
    }

    override fun onReturnNFCApduResult(arg0: Boolean, arg1: String, arg2: Int) {
        TRACE.d(("onReturnNFCApduResult(boolean arg0, String arg1, int arg2):$arg0\n").toString() + arg1 + "\n" + arg2)
    }

    override fun onReturnPowerOffNFCResult(arg0: Boolean) {
        TRACE.d(" onReturnPowerOffNFCResult(boolean arg0) :$arg0")
    }

    override fun onReturnPowerOnNFCResult(arg0: Boolean, arg1: String, arg2: String, arg3: Int) {
        TRACE.d((("onReturnPowerOnNFCResult(boolean arg0, String arg1, String arg2, int arg3):$arg0\n").toString() + arg1 + "\n") + arg2 + "\n" + arg3)
    }

    override fun onCbcMacResult(result: String) {
        TRACE.d("onCbcMacResult(String result):$result")
    }

    override fun onReadBusinessCardResult(arg0: Boolean, arg1: String) {
        TRACE.d(" onReadBusinessCardResult(boolean arg0, String arg1):$arg0\n$arg1")
    }

    override fun onWriteBusinessCardResult(arg0: Boolean) {
        TRACE.d(" onWriteBusinessCardResult(boolean arg0):$arg0")
    }

    override fun onConfirmAmountResult(arg0: Boolean) {
        TRACE.d("onConfirmAmountResult(boolean arg0):$arg0")
    }

    override fun onQposIsCardExist(cardIsExist: Boolean) {
        TRACE.d("onQposIsCardExist(boolean cardIsExist):$cardIsExist")

    }

    override fun onSearchMifareCardResult(arg0: Hashtable<String, String>) {
        if (arg0 != null) {
            TRACE.d("onSearchMifareCardResult(Hashtable<String, String> arg0):$arg0")
            val statuString = arg0["status"]
            val cardTypeString = arg0["cardType"]
            val cardUidLen = arg0["cardUidLen"]
            val cardUid = arg0["cardUid"]
            val cardAtsLen = arg0["cardAtsLen"]
            val cardAts = arg0["cardAts"]
            val ATQA = arg0["ATQA"]
            val SAK = arg0["SAK"]
        }
    }

    override fun onBatchReadMifareCardResult(
        msg: String,
        cardData: Hashtable<String, List<String>>
    ) {
        if (cardData != null) {
            TRACE.d("onBatchReadMifareCardResult(boolean arg0):$msg$cardData")
        }
    }

    override fun onBatchWriteMifareCardResult(
        msg: String,
        cardData: Hashtable<String, List<String>>
    ) {
        if (cardData != null) {
            TRACE.d("onBatchWriteMifareCardResult(boolean arg0):$msg$cardData")
        }
    }

    override fun onSetBuzzerResult(arg0: Boolean) {
        TRACE.d("onSetBuzzerResult(boolean arg0):$arg0")
    }

    override fun onSetBuzzerTimeResult(b: Boolean) {
        TRACE.d("onSetBuzzerTimeResult(boolean b):$b")
    }

    override fun onSetBuzzerStatusResult(b: Boolean) {
        TRACE.d("onSetBuzzerStatusResult(boolean b):$b")
    }

    override fun onGetBuzzerStatusResult(s: String) {
        TRACE.d("onGetBuzzerStatusResult(String s):$s")
    }

    override fun onSetManagementKey(arg0: Boolean) {
        TRACE.d("onSetManagementKey(boolean arg0):$arg0")

    }

    override fun onReturnUpdateIPEKResult(arg0: Boolean) {
        TRACE.d("onReturnUpdateIPEKResult(boolean arg0):$arg0")
    }

    override fun onReturnUpdateEMVRIDResult(arg0: Boolean) {
        TRACE.d("onReturnUpdateEMVRIDResult(boolean arg0):$arg0")
    }

    override fun onReturnUpdateEMVResult(arg0: Boolean) {
        TRACE.d("onReturnUpdateEMVResult(boolean arg0):" + arg0);
    }

    override fun onBluetoothBoardStateResult(arg0: Boolean) {
        TRACE.d("onBluetoothBoardStateResult(boolean arg0):$arg0")
    }

    @SuppressLint("MissingPermission")
    override fun onDeviceFound(arg0: BluetoothDevice) {
        if (arg0 != null && arg0.name != null) {
            TRACE.d("onDeviceFound(BluetoothDevice arg0):" + arg0.name + ":" + arg0.toString())
                val itm: MutableMap<String, Any> = HashMap()
                itm["ICON"] =
                    if (arg0.bondState == BluetoothDevice.BOND_BONDED) Integer.valueOf(com.lovisgod.payble_qpos_sdk.R.drawable.bluetooth_blue) else Integer.valueOf(
                        com.lovisgod.payble_qpos_sdk.R.drawable.bluetooth_blue_unbond
                    )
                itm["TITLE"] = arg0.name + "(" + arg0.address + ")"
                itm["ADDRESS"] = arg0.address

            val address = arg0.address
            var name = arg0.name
            name += address + "\n"
            TRACE.d("found new device$name")
            if (arg0.name.contains("MPOS") ||
                arg0.name.contains("Payble") ||
                arg0.name.lowercase().contains("payble")) {
                emvEvents.onDeviceFound(device = itm)
            }
        } else {
            emvEvents.onDeviceNotFound()
            TRACE.d("Don't found new device")
        }
    }

    override fun onSetSleepModeTime(arg0: Boolean) {
        TRACE.d("onSetSleepModeTime(boolean arg0):$arg0")
    }

    override fun onReturnGetEMVListResult(arg0: String) {
        TRACE.d("onReturnGetEMVListResult(String arg0):$arg0")
    }

    override fun onWaitingforData(arg0: String) {
        TRACE.d("onWaitingforData(String arg0):$arg0")
    }

    override fun onRequestDeviceScanFinished() {
        TRACE.d("onRequestDeviceScanFinished()")
        //            Toast.makeText(CheckActivity.this, R.string.scan_over, Toast.LENGTH_SHORT).show();
    }

    override fun onRequestUpdateKey(arg0: String) {
        TRACE.d("onRequestUpdateKey(String arg0):$arg0")
    }

    override fun onReturnGetQuickEmvResult(arg0: Boolean) {
        TRACE.d("onReturnGetQuickEmvResult(boolean arg0):$arg0")
        if (arg0) {
            pos.setQuickEmv(true)
        }
    }

    override fun onQposDoGetTradeLogNum(arg0: String) {
        TRACE.d("onQposDoGetTradeLogNum(String arg0):$arg0")
        val a = arg0.toInt(16)
        if (a >= 188) {
            return
        }
    }

    override fun onQposDoTradeLog(arg0: Boolean) {
        TRACE.d("onQposDoTradeLog(boolean arg0) :$arg0")
        if (arg0) {
           TRACE.d("clear log success!")
        } else {
            TRACE.d("clear log fail!")
        }
    }

    override fun onAddKey(arg0: Boolean) {
        TRACE.d("onAddKey(boolean arg0) :$arg0")
        if (arg0) {
           TRACE.d("ksn add 1 success")
        } else {
            TRACE.d("ksn add 1 failed")
        }
    }

    override fun onEncryptData(resultTable: Hashtable<String, String>) {
        if (resultTable != null) {
            TRACE.d("onEncryptData(String arg0) :$resultTable")
        }
    }

    override fun onQposKsnResult(arg0: Hashtable<String, String>) {
        TRACE.d("onQposKsnResult(Hashtable<String, String> arg0):$arg0")
        val pinKsn = arg0["pinKsn"]
        val trackKsn = arg0["trackKsn"]
        val emvKsn = arg0["emvKsn"]
        TRACE.d("get the ksn result is :pinKsn$pinKsn\ntrackKsn$trackKsn\nemvKsn$emvKsn")
    }

    override fun onQposDoGetTradeLog(arg0: String, arg1: String) {
        var arg1 = arg1
        TRACE.d(("onQposDoGetTradeLog(String arg0, String arg1):$arg0\n").toString() + arg1)
        arg1 = QPOSConversionUtil.convertHexToString(arg1)
        TRACE.d("orderId:$arg1\ntrade log:$arg0")
    }

    override fun onRequestDevice() {
//        val deviceList: List<UsbDevice> = getPermissionDeviceList()
//        val mManager = getSystemService(Context.USB_SERVICE) as UsbManager
//        for (i in deviceList.indices) {
//            val usbDevice = deviceList[i]
//            if (usbDevice.vendorId == 2965 || usbDevice.vendorId == 0x03EB) {
//                if (mManager.hasPermission(usbDevice)) {
//                    pos.setPermissionDevice(usbDevice)
//                } else {
//                    devicePermissionRequest(mManager, usbDevice)
//                }
//            }
//        }
    }

    override fun onGetKeyCheckValue(checkValue: Hashtable<String, String>) {
        super.onGetKeyCheckValue(checkValue)
        val MKSK_TMK_KCV = "MKSK_TMK_KCV : " + checkValue["MKSK_TMK_KCV"]
        val DUKPT_PIN_IPEK_KCV = "DUKPT_PIN_IPEK_KCV : " + checkValue["DUKPT_PIN_IPEK_KCV"]
        val DUKPT_PIN_KSN = "DUKPT_PIN_KSN : " + checkValue["DUKPT_PIN_KSN"]
        val DUKPT_EMV_IPEK_KCV = "DUKPT_EMV_IPEK_KCV : " + checkValue["DUKPT_EMV_IPEK_KCV"]
        val DUKPT_EMV_KSN = "DUKPT_EMV_KSN : " + checkValue["DUKPT_EMV_KSN"]
        val DUKPT_TRK_IPEK_KCV = "DUKPT_TRK_IPEK_KCV : " + checkValue["DUKPT_TRK_IPEK_KCV"]
        val DUKPT_TRK_KSN = "DUKPT_TRK_KSN : " + checkValue["DUKPT_TRK_KSN"]
        val MKSK_PIK_KCV = "MKSK_PIK_KCV : " + checkValue["MKSK_PIK_KCV"]
        val MKSK_TDK_KCV = "MKSK_TDK_KCV : " + checkValue["MKSK_TDK_KCV"]
        val MKSK_MCK_KCV = "MKSK_MCK_KCV : " + checkValue["MKSK_MCK_KCV"]
        val TCK_KCV = "TCK_KCV : " + checkValue["TCK_KCV"]
        val MAGK_KCV = "MAGK_KCV : " + checkValue["MAGK_KCV"]
        val stringBuffer = StringBuffer()
        stringBuffer.append(MKSK_TMK_KCV)
        stringBuffer.append("\n")
        stringBuffer.append(DUKPT_PIN_IPEK_KCV)
        stringBuffer.append("\n")
        stringBuffer.append(DUKPT_PIN_KSN)
        stringBuffer.append("\n")
        stringBuffer.append(DUKPT_EMV_IPEK_KCV)
        stringBuffer.append("\n")
        stringBuffer.append(DUKPT_EMV_KSN)
        stringBuffer.append("\n")
        stringBuffer.append(DUKPT_TRK_IPEK_KCV)
        stringBuffer.append("\n")
        stringBuffer.append(DUKPT_TRK_KSN)
        stringBuffer.append("\n")
        stringBuffer.append(MKSK_PIK_KCV)
        stringBuffer.append("\n")
        stringBuffer.append(MKSK_TDK_KCV)
        stringBuffer.append("\n")
        stringBuffer.append(MKSK_MCK_KCV)
        stringBuffer.append("\n")
        stringBuffer.append(TCK_KCV)
        stringBuffer.append("\n")
        stringBuffer.append(MAGK_KCV)
    }

    override fun onGetDevicePubKey(hashtable: Hashtable<String, String>) {
        super.onGetDevicePubKey(hashtable)
        TRACE.d("onGetDevicePubKey(clearKeys):$hashtable")
        TRACE.d("DevicePubbicKey: \n${hashtable["modulus"]}")
    }

    override fun onTradeCancelled() {
        TRACE.d("onTradeCancelled")
    }

    override fun onReturnConverEncryptedBlockFormat(result: String) {
    }

    override fun onFinishMifareCardResult(arg0: Boolean) {
        TRACE.d("onFinishMifareCardResult(boolean arg0):$arg0")
    }

    override fun onVerifyMifareCardResult(arg0: Boolean) {
        TRACE.d("onVerifyMifareCardResult(boolean arg0):$arg0")
    }

    override fun onReadMifareCardResult(arg0: Hashtable<String, String>) {
        TRACE.d("onReadMifareCardResult(Hashtable<String, String> arg0):")
        if (arg0 != null) {
            TRACE.d("onReadMifareCardResult(Hashtable<String, String> arg0):$arg0")
            val addr = arg0["addr"]
            val cardDataLen = arg0["cardDataLen"]
            val cardData = arg0["cardData"]
           TRACE.d("addr:$addr\ncardDataLen:$cardDataLen\ncardData:$cardData")
        } else {
            TRACE.d("Failed")
        }
    }

    override fun onWriteMifareCardResult(arg0: Boolean) {
        TRACE.d("onWriteMifareCardResult(boolean arg0):$arg0")
        if (arg0) {
            TRACE.d("Write success")
        } else {
            TRACE.d("Write fail")
        }
    }

    override fun onOperateMifareCardResult(arg0: Hashtable<String, String>) {
        TRACE.d("onOperateMifareCardResult(Hashtable<String, String> arg0):")
        if (arg0 != null) {
            TRACE.d("onOperateMifareCardResult(Hashtable<String, String> arg0):$arg0")
            val cmd = arg0["Cmd"]
            val blockAddr = arg0["blockAddr"]
            TRACE.d("Cmd:$cmd\nBlock Addr:$blockAddr")
        } else {
           TRACE.d("Operation failed")
        }
    }

    override fun getMifareCardVersion(arg0: Hashtable<String, String>) {
        if (arg0 != null) {
            TRACE.d("getMifareCardVersion(Hashtable<String, String> arg0):$arg0")
            val verLen = arg0["versionLen"]
            val ver = arg0["cardVersion"]
            TRACE.d("versionLen:$verLen\nverison:$ver")
        } else {
            TRACE.d("get mifare UL version failed")
        }
    }

    override fun getMifareFastReadData(arg0: Hashtable<String, String>) {
        if (arg0 != null) {
            TRACE.d("getMifareFastReadData(Hashtable<String, String> arg0):$arg0")
            val startAddr = arg0["startAddr"]
            val endAddr = arg0["endAddr"]
            val dataLen = arg0["dataLen"]
            val cardData = arg0["cardData"]
            TRACE.d("startAddr:$startAddr\nendAddr:$endAddr\ndataLen:$dataLen\ncardData:$cardData")
        } else {
            TRACE.d("read fast UL failed")
        }
    }

    override fun getMifareReadData(arg0: Hashtable<String, String>) {
        if (arg0 != null) {
            TRACE.d("getMifareReadData(Hashtable<String, String> arg0):$arg0")
            val blockAddr = arg0["blockAddr"]
            val dataLen = arg0["dataLen"]
            val cardData = arg0["cardData"]
            TRACE.d("blockAddr:$blockAddr\ndataLen:$dataLen\ncardData:$cardData")
        } else {
            TRACE.d("read mifare UL failed")
        }
    }

    override fun writeMifareULData(arg0: String) {
        if (arg0 != null) {
            TRACE.d("writeMifareULData(String arg0):$arg0")
            TRACE.d("addr:$arg0")
        } else {
           TRACE.d("write UL failed")
        }
    }

    override fun verifyMifareULData(arg0: Hashtable<String, String>) {
        if (arg0 != null) {
            TRACE.d("verifyMifareULData(Hashtable<String, String> arg0):$arg0")
            val dataLen = arg0["dataLen"]
            val pack = arg0["pack"]
            TRACE.d("dataLen:$dataLen\npack:$pack")
        } else {
            TRACE.d("verify UL failed")
        }
    }

    override fun onGetSleepModeTime(arg0: String) {
        if (arg0 != null) {
            TRACE.d("onGetSleepModeTime(String arg0):$arg0")
            val time = arg0.toInt(16)
           TRACE.d("time is ： $time seconds")
        } else {
           TRACE.d("get the time is failed")
        }
    }

    override fun onGetShutDownTime(arg0: String) {
        if (arg0 != null) {
            TRACE.d("onGetShutDownTime(String arg0):$arg0")
           TRACE.d("shut down time is : " + arg0.toInt(16) + "s")
        } else {
           TRACE.d("get shutdown time failed")
        }
    }

    override fun onQposDoSetRsaPublicKey(arg0: Boolean) {
        TRACE.d("onQposDoSetRsaPublicKey(boolean arg0):$arg0")
        if (arg0) {
           TRACE.d("set rsa is successed!")
        } else {
           TRACE.d("set rsa is failed!")
        }
    }

    override fun onQposGenerateSessionKeysResult(arg0: Hashtable<String, String>) {
        if (arg0 != null) {
            TRACE.d("onQposGenerateSessionKeysResult(Hashtable<String, String> arg0):$arg0")
            val rsaFileName = arg0["rsaReginString"]
            val enPinKeyData = arg0["enPinKey"]
            val enKcvPinKeyData = arg0["enPinKcvKey"]
            val enCardKeyData = arg0["enDataCardKey"]
            val enKcvCardKeyData = arg0["enKcvDataCardKey"]
            TRACE.d("rsaFileName:$rsaFileName\nenPinKeyData:$enPinKeyData\nenKcvPinKeyData:$enKcvPinKeyData\nenCardKeyData:$enCardKeyData\nenKcvCardKeyData:$enKcvCardKeyData")
        } else {
           TRACE.d("get key failed,pls try again!")
        }
    }

    override fun transferMifareData(arg0: String) {
        TRACE.d("transferMifareData(String arg0):$arg0")
        if (arg0 != null) {
            TRACE.d("response data:$arg0")
        } else {
           TRACE.d("transfer data failed!")
        }
    }

    override fun onReturnRSAResult(arg0: String) {
        TRACE.d("onReturnRSAResult(String arg0):$arg0")
        if (arg0 != null) {
           TRACE.d("rsa data:\n$arg0")
        } else {
            TRACE.d("get the rsa failed")
        }
    }

    override fun onRequestNoQposDetectedUnbond() {
        // TODO Auto-generated method stub
        TRACE.d("onRequestNoQposDetectedUnbond()")
    }

    override fun onReturnDeviceCSRResult(re: String) {
        TRACE.d("onReturnDeviceCSRResult:$re")
    }

    override fun onReturnStoreCertificatesResult(re: Boolean) {
        TRACE.d("onReturnStoreCertificatesResult:$re")
    }

    override fun onReturnAnalyseDigEnvelop(result: String) {
        super.onReturnAnalyseDigEnvelop(result)
//        verifySignatureCommand = ParseASN1Util.addTagToCommand(verifySignatureCommand, "KA", result)
//        if (pedvVerifySignatureCommand != null) {
//            pedvVerifySignatureCommand =
//                ParseASN1Util.addTagToCommand(pedvVerifySignatureCommand, "KA", result)
//            TRACE.i("send key encryption command to RMKS is $pedvVerifySignatureCommand")
//        }
//        TRACE.i("send key encryption command to RMKS is $verifySignatureCommand")
//        val response =
//            "[AOPEDK;ANY;KN0;KB30819D0201013181973081941370413031313242315458303045303130304B5331384646464630303030303030303031453030303030414332313038323435443442443733373445443932464142363838373438314544363034344137453635433239463132393739383931384441394434353631443235324143414641020102040AFFFF0000000001E00000020100130A4473707265616442444B04021D58;KA0D0B2F2F3178D4045C1363274890494664B23D32BABEA47E5DB42F15C06816107FD293BAFF7371119F0B11B685A29D40DE78D397F9629A56112629452A9525F5261F8BDCA168328C49ACCFF0133C90E91AFCCA1E18178EBBA5E0BFA054B09514BA87EE05F2E4837D2C74E00BFD3B14EB708598517F357F79AA34C89DFEA9F59B6D3CECABA6C211809400DE9D0B0CA09384FDD834B8BFD416C4B09D32B3F5E45001F18E5C3116A0FFD8E0C6ACE567FCCE1AC909FD038FB58F16BB32163866CD9DCB4B131A394757638111B2CF3DC968D58CBAA95279BEFF697C0D92C6A42248B53A3E56E595AD128EDB50710BDBFFCB113A7DC4ECBCE8668482CBFD22CD7B2E42;RDB077A03C07C94F161842AA0C4831E0EF;CE1;AP308205A906092A864886F70D010702A082059A30820596020149310D300B06096086480165030402013082032506092A864886F70D010703A082031604820312020100318201FB308201F70201003081A830819C310B3009060355040613025553310B300906035504080C0254583114301206035504070C0B53414E20414E544F4E494F31133011060355040A0C0A56697274754372797074311B3019060355040C0C12447370726561642044657669636573204341311B301906035504410C12447370726561642044657669636573204341311B301906035504030C1244737072656164204465766963657320434102074EB0D600009880304306092A864886F70D0101073036300B0609608648016503040201301806092A864886F70D010108300B0609608648016503040201300D06092A864886F70D01010904000482010092583A07F6280625EE4CA043E3245F2CD6CCA8BAE6E198F4046A5DDE055723D2591A84DDCA4D7F7BB1B179881FD9EC4E33ED22333A9008DAEB3C3B1D7143D1953F2363BEA4C0D2592667C3468F228F856A95A6DCA1FA9CA0AB05D25DC612E7E2BF2AE3012D22C78BB7224C8C8E02146929937C3DF9FA3589B2A486C132477ACFA50BE09528FCBFDA43079AF54C050843BE4BDE701D246D8D8A4C947F12AFD97A66010459BBAE4ED627F687CC3E6DC30B5B35FE3564D9FB07F501B57A73A70AB9C3398E14391B16A5FE45C374984219F0B3A3265A82D3F5A48CEEF3998DCEA59F1CC5821B51605C66C8FD2687778C84B51CCE51C1FBFA876F978E0A9546C425FF3082010C06092A864886F70D010701301406082A864886F70D03070408C8FA8F2094E103118081E85816DF38AEC7C0E569C011DB7212278A767C8934770C7E994E9508E256B693973FBB4B47A78A9F6B1AB2D326CC2A76A53E3731B8A8128B1DE4BEDCCA51E0E740C1A474C21C8CF4A4726F4FBE0DC5CE41C4DB7A2CDBB2EF7B2C0F61B50E34A1A327A5069EB23524DB0D8119C4C407B90277B806288ECAC2826AF8AF6D092B29E90C03554986F38345B6BB247BC1498C2185661BDE318ADECAF199E798D70A058305F686ECC3A267D28EED6052483401EB5B5B84F897CAEA7968B8EEAB23F465CE3F1E7F7F7E402D1AA681D76D34CF9EC0B6BBBE9A513B8C42E5EA5319E218AC996F87767966DBD8F8318202573082025302014930819C308190310B3009060355040613025553310B300906035504080C0254583114301206035504070C0B53414E20414E544F4E494F31133011060355040A0C0A5669727475437279707431173015060355040C0C0E44737072656164204B44482043413117301506035504410C0E44737072656164204B44482043413117301506035504030C0E44737072656164204B444820434102074EB0D60000987E300B0609608648016503040201A0818E301806092A864886F70D010903310B06092A864886F70D0107033020060A2A864886F70D01091903311204104CDCEDD916AAACEEAE548A1C5B0A0EAA301F06092A864886F70D0107013112041041303031364B30544E30304530303030302F06092A864886F70D01090431220420A0E06A133DA8D4A5EC5A2E51E468B470B19E13834019A0C2563BA39308660A1F300D06092A864886F70D0101010500048201003BA0F51DC5B3400E5CD29429663008713C3B61DE0C053590296421635218AEB228A1802C971B18CCF0A137D66FE07B08A0B2A592F11557CC401C353C859E1B82C4BAE146F8AC2955BD1326A3482B173E5589B321FBA0517DCA071F120D0940DC7B8CD33C861E1403CCBD7C3203F1609D261D38B415A0BF234CC9370D18B1004D89BE4C7C4631C7A5D3A1010F0371E25F70B8000D5B94C946571D0F6A730DEF57950AED18839B38B0FF6497D03E960194CF3F113C57575F62E8299FCDE855A1BD36ECE5CAF3DC9F942387A76A329715EC09FDBED3C4FACA06160D538EC00D0166D46152D61F6C665F749E91A0E70E532CE726525B946ACD81510FF47146F00994;]"
//        val KA: String = ParseASN1Util.parseToken(response, "KA")
//        KB = ParseASN1Util.parseToken(response, "KB")
//        val signatureData =
//            "a57e821386de1038b1a12dc22fa59ce317625680c523bd66bf2b9f840aebe52d020e07105d4107eeb05edd560d0345cd73ce2b68dbf19c61f9d56fbd1ddf9222c47956595b773c88eb7ec4577fb17053d42acf64f3e5c38ff325cdac7b689df029299087b69211e61bdfc22e329eb287456f83ef6c25e84fe1324e36ee85ba7e3accb79eb8ab7b270916a28a42a867e0e050c6950100c90daddb1f421444d16accb6005a312c3273c2f1b28f0c77456ae875081ae594d26139efd267c8dafa15e1b6cf961f3acdb92b26777127f474d24d57611b29f01dec062c02d720c4e759e1757f85ee39e74e05e23aa0aed53d62d05a902a6539a3e986e6dd237888ff92"
//        var verifyResult: Boolean =
//            pos.authenticServerResponse(HexStringToByteArray(KA), signatureData)
//        verifyResult = true
//        if (verifyResult) {
//            if (response.contains("AP")) {
//                val AP: String = ParseASN1Util.parseToken(response, "AP")
//                ParseASN1Util.parseASN1new(AP.replace("A081", "3081"))
//                val nonce: String = ParseASN1Util.getNonce()
//                val header: String = ParseASN1Util.getHeader()
//                val digist: String = ParseASN1Util.getDigest()
//                val encryptData: String = ParseASN1Util.getEncryptData()
//                ParseASN1Util.parseASN1new(encryptData.substring(6))
//                val signData: String = ParseASN1Util.getSignData()
//                val encryptDataWith3des: String = ParseASN1Util.getEncryptDataWith3Des()
//                val IV: String = ParseASN1Util.getIVStr()
//                val clearData =
//                    "A0818e301806092a864886f70d010903310b06092a864886f70d0107033020060a2a864886f70d01091903311204104cdcedd916aaaceeae548a1c5b0a0eaa301f06092a864886f70d0107013112041041303031364b30544e30304530303030302f06092a864886f70d01090431220420a0e06a133da8d4a5ec5a2e51e468b470b19e13834019a0c2563ba39308660a1f"
//                val envelop: String =
//                    com.dspread.demoui.activity.PaymentActivity.getDigitalEnvelopStr(
//                        encryptData,
//                        encryptDataWith3des,
//                        "01",
//                        clearData,
//                        signData,
//                        IV
//                    )
//                //the api callback is onRequestUpdateWorkKeyResult
//                pos.loadSessionKeyByTR_34(envelop)
//            } else {
//                statusEditText.setText("signature verification successful.")
//                ParseASN1Util.parseASN1new(KB)
//                val data: String = ParseASN1Util.getTr31Data()
//                //the api callback is onReturnupdateKeyByTR_31Result
//                pos.updateKeyByTR_31(1, data)
//            }
//        } else {
//            statusEditText.setText("signature verification failed.")
//        }
    }

    private fun buildCvmPinBlock(
        value: Hashtable<String, String?>,
        pin: String
    ): String {
        var pin = pin
        val randomData =
            if (value["RandomData"] == null) "" else value["RandomData"]!!
        val pan = if (value["PAN"] == null) "" else value["PAN"]!!
        val AESKey = if (value["AESKey"] == null) "" else value["AESKey"]!!
        val isOnline =
            if (value["isOnlinePin"] == null) "" else value["isOnlinePin"]!!
        val pinTryLimit =
            if (value["pinTryLimit"] == null) "" else value["pinTryLimit"]!!
        //iso-format4 pinblock
        val pinLen = pin.length
        pin = "4" + Integer.toHexString(pinLen) + pin
        for (i in 0 until 14 - pinLen) {
            pin = pin + "A"
        }
        pin += randomData.substring(0, 16)
        var panBlock = ""
        val panLen = pan.length
        var m = 0
        if (panLen < 12) {
            panBlock = "0"
            for (i in 0 until 12 - panLen) {
                panBlock += "0"
            }
            panBlock = panBlock + pan + "0000000000000000000"
        } else {
            m = pan.length - 12
            panBlock = m.toString() + pan
            for (i in 0 until 31 - panLen) {
                panBlock += "0"
            }
        }
        val pinBlock1 = AESUtil.encrypt(AESKey, pin)
        pin = Util.xor16(
            HexStringToByteArray(pinBlock1),
            HexStringToByteArray(panBlock)
        )
        return AESUtil.encrypt(AESKey, pin)
    }

    private fun getPosInfo(info: String) {

        if ("posid" == info) {
            TRACE.d("get pos id id")
            pos.getQposId()
        } else if ("posinfo" == info) {
            pos.getQposInfo()
        } else if ("updatekey" == info) {
            pos.updateCheckValue
        } else if ("keycheckvalue" == info) {
            val keyIdex = getKeyIndex()
            pos.getKeyCheckValue(keyIdex, QPOSService.CHECKVALUE_KEYTYPE.DUKPT_MKSK_ALLTYPE)
        }
    }

    private fun updatePosInfo(updatePosInfo: String) {
        if ("updateIpeK" == updatePosInfo) {

            val keyIndex = getKeyIndex()
            val ipekGrop = "0$keyIndex"
            pos.doUpdateIPEKOperation(
                ipekGrop,
                "09118012400705E00000",
                "C22766F7379DD38AA5E1DA8C6AFA75AC",
                "B2DE27F60A443944",
                "09118012400705E00000",
                "C22766F7379DD38AA5E1DA8C6AFA75AC",
                "B2DE27F60A443944",
                "09118012400705E00000",
                "C22766F7379DD38AA5E1DA8C6AFA75AC",
                "B2DE27F60A443944"
            )
        } else if ("setMasterkey" == updatePosInfo) {
            val keyIndex = getKeyIndex()
            pos.setMasterKey( EncryptedPrefsHelper(context).getString(EncryptedPrefsHelper.MASTER_KEY), "08D7B4FB629D0885", keyIndex)
        } else if ("updateWorkkey" == updatePosInfo) {
            val keyIndex = getKeyIndex()
            pos.updateWorkKey(
                EncryptedPrefsHelper(context).getString(EncryptedPrefsHelper.PIN_KEY), "08D7B4FB629D0885",  //PIN KEY
                EncryptedPrefsHelper(context).getString(EncryptedPrefsHelper.PIN_KEY), "08D7B4FB629D0885",  //TRACK KEY
                EncryptedPrefsHelper(context).getString(EncryptedPrefsHelper.PIN_KEY), "08D7B4FB629D0885",  //MAC KEY
                keyIndex, 5
            )
        } else if ("updateFirmware" == updatePosInfo) {

//            updateFirmware()
        } else if ("updateEmvByXml" == updatePosInfo) {
            pos.updateEMVConfigByXml(
                String(
                    FileUtils.readAssetsLine(
                        "emv_profile_tlv_cit.xml",
                        context
                    )
                )
            )
        }
    }

}
