package com.lovisgod.payble_qpos_sdk

import android.app.Application
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.lovisgod.kozen_p.PaybleConstants
import com.lovisgod.payble_qpos_sdk.qpos_mini.QposInitializer

class MainActivity : AppCompatActivity(),  EMVEvents{
    lateinit var testInitBtn: Button
    lateinit var testDoTransBtn: Button
    lateinit var testSearchDeviceBtn: Button
    lateinit var testConnectDeviceBtn: Button
    lateinit var testGetAgentDetailBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        testConnectDeviceBtn = findViewById(R.id.test_connect_device)
        testSearchDeviceBtn = findViewById(R.id.test_search_device)
        testInitBtn = findViewById(R.id.test_initialize_pos)
        testGetAgentDetailBtn = findViewById(R.id.test_get_agent_details)
        testDoTransBtn = findViewById(R.id.test_trans)

        handleClicks()
    }


   fun handleClicks(){
     testGetAgentDetailBtn.setOnClickListener {
         QposInitializer.getInstance().getAgentDetails("", this, this)
     }

     testInitBtn.setOnClickListener {
         QposInitializer.getInstance().initializeQpos(SampleApplication(), this, this)
     }

     testSearchDeviceBtn.setOnClickListener {
         QposInitializer.getInstance().searchForDevices(this)
     }

     testConnectDeviceBtn.setOnClickListener {
         QposInitializer.getInstance().connectBluetoothDevice("30:3D:51:46:CF:C6")
     }

     testDoTransBtn.setOnClickListener {
         QposInitializer.getInstance().setTransactionParam("10000", "566")
         QposInitializer.getInstance().startTrade()
     }
   }

    override fun onInsertCard() {
        println("Insert card")
    }

    override fun onRemoveCard(isContactlessTransLimit: Boolean, message: String) {
        println(message)
        QposInitializer.getInstance().resetPosSdk()
    }

    override fun onCardRead(pan: String, cardType: PaybleConstants.CardType) {
        println("Pan::: ${pan}:::: card type::: ${cardType.name}")
    }

    override fun onCardDetected(contact: Boolean) {
        println("card detected contact is:::: $contact")
    }

    override fun onEmvProcessing(message: String) {
        println(message)
    }

    override fun onPinInput(): String? {
        return "1234"
    }

    override fun onEmvProcessed(data: Any) {
       println("data is $data")
    }

    override fun onUserCanceled(message: String) {
       println("user canceled $message")
    }

    override fun onTransactionCancelled(message: String) {
        println("transaction canceled $message")
    }

    override fun offlinePinTryExceeded() {
       println("offline pin try exceeded")
    }

    override fun onPinInputText(text: String) {
        println("pin input text $text")
    }

    override fun onMessage(message: String) {
        println("message is $message")
    }

    override fun nodeviceDetected(message: String) {
       println("no device detected:: $message")
    }

    override fun onDeviceConnected(message: String, deviceName: String) {
       println("on device connected $message::::: device name ::: $deviceName")
    }

    override fun onDeviceDisconnected(message: String) {
        println(message)
    }

    override fun onDeviceConnectFailed(message: String) {
        println("device connection failed::: $message")
    }

    override fun onDeviceBondSucceed(message: String) {
        println("device bond successful::::$message")
    }

    override fun onDeviceBonding(message: String) {
        println(message)
    }

    override fun onDeviceFound(device: MutableMap<String, Any>) {
        println("device found :::: $device")
    }

    override fun onDeviceNotFound() {
       println("no device found")
    }

    override fun onAgentDetailsDownloading() {
        Toast.makeText(this, "Agent details downloading please wait", Toast.LENGTH_SHORT).show()
    }

    override fun onAgentDetailsDownloaded() {
        Toast.makeText(this, "Agent details downloaded", Toast.LENGTH_LONG).show()
    }

    override fun onAgentDetailsDownloadError(message: String) {
        Toast.makeText(this, "Agent details download Error", Toast.LENGTH_LONG).show()
    }

    override fun onAgentTransactionOnline() {
        Toast.makeText(this, "Transaction going online", Toast.LENGTH_SHORT).show()
    }

    override fun onAgentTransactionOnlineResponse(response: Any) {
        Toast.makeText(this, "Transaction response $response", Toast.LENGTH_LONG).show()
    }

    override fun onAgentTransactionError(message: String) {
        Toast.makeText(this, "Transaction Error", Toast.LENGTH_LONG).show()
    }
}

class SampleApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        QposInitializer.getInstance().initPayble("", "", applicationContext)
    }
}