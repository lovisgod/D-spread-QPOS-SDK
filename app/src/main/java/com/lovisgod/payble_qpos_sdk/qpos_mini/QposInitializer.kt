package com.lovisgod.payble_qpos_sdk.qpos_mini

import android.Manifest
import android.app.Activity
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dspread.print.util.TRACE
import com.dspread.xpos.QPOSService
import com.dspread.xpos.QPOSService.CardTradeMode
import com.lovisgod.kozen_p.PaybleConstants
import com.lovisgod.payble_qpos_sdk.EMVEvents
import com.lovisgod.payble_qpos_sdk.network.RetrofitInstance
import com.lovisgod.payble_qpos_sdk.network.models.AgentData
import com.lovisgod.payble_qpos_sdk.network.models.TerminalKeyResponse
import com.lovisgod.payble_qpos_sdk.network.models.toJson
import com.lovisgod.payble_qpos_sdk.utils.EncryptedPrefsHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class QposInitializer private constructor() {
    lateinit var qposApplication: Application
//    lateinit var qposContext: Context
    private val BLUETOOTH_CODE: Int = 100
    private val LOCATION_CODE = 101
    lateinit var pos: QPOSService
    private var handler: Handler? = null

    val api = RetrofitInstance.api


//    fun assignTerminal(agentId: String, serialNumber: String) {
//        // make api call to assign terminal to agent
//    }

    fun initPayble(api_key: String, merchant_id: String) {
        PaybleConstants.api_key = api_key
        PaybleConstants.mid = merchant_id
    }

    fun getAgentDetails(agentId: String, context: Context, emvEvents: EMVEvents) {
        // download agent details

        emvEvents.onAgentDetailsDownloading()

        val call = api.getAgentDetails(agentId, PaybleConstants.api_key, PaybleConstants.mid)

        call.enqueue(object : Callback<AgentData> {
            override fun onResponse(call: Call<AgentData>, response: Response<AgentData>) {
                if (response.isSuccessful) {
                    val agent = response.body()
                    Log.d("get agent details", "Post: $agent")
                    if (agent?.data != null) {
                        val sharedPreferences = EncryptedPrefsHelper(context)
                        sharedPreferences.putString(EncryptedPrefsHelper.AGENT_DATA, agent.toJson())
                        initTerminalDetails(agent.data.terminals.terminalId, context, emvEvents)
                    }
                } else {
                    emvEvents.onAgentDetailsDownloadError(message = "Error: ${response.code()}")
                    Log.e("get agent details", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AgentData>, t: Throwable) {
                emvEvents.onAgentDetailsDownloadError(message = "Failure: ${t.message}")
                Log.e("get agent details", "Failure: ${t.message}")
            }
        })

    }


    fun initTerminalDetails(terminalId: String, context: Context, emvEvents: EMVEvents) {
        // download terminal details from middleware

        val call = api.getKeys(terminalId)

        call.enqueue(object : Callback<TerminalKeyResponse> {
            override fun onResponse(call: Call<TerminalKeyResponse>, response: Response<TerminalKeyResponse>) {
                if (response.isSuccessful) {
                    val terminal = response.body()
                    emvEvents.onAgentDetailsDownloaded()
                    Log.d("get terminal details", "terminal: $terminal")
                    if (terminal?.data != null) {
                        val sharedPreferences = EncryptedPrefsHelper(context)
                        sharedPreferences.putString(EncryptedPrefsHelper.TERMINAL_DATA, terminal.toJson())
                        val sessionKey =  terminal.data.sessionKey// get session key
                        val  pinKey = terminal.data.pinKey // get pinKey
                        val masterKey = terminal.data.masterKey // get master key
                        sharedPreferences.putString(EncryptedPrefsHelper.MASTER_KEY, masterKey)
                        sharedPreferences.putString(EncryptedPrefsHelper.SESSION_KEY, sessionKey)
                        sharedPreferences.putString(EncryptedPrefsHelper.PIN_KEY, pinKey)

                    }
                } else {
                    emvEvents.onAgentDetailsDownloadError(message = "Error: ${response.code()}")
                    Log.e("get terminal details", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<TerminalKeyResponse>, t: Throwable) {
                emvEvents.onAgentDetailsDownloadError(message = "Failure: ${t.message}")
                Log.e("get terminal details", "Failure: ${t.message}")
            }
        })
    }

    /**
     * Initialize qpos_sdk
     **/
    fun initializeQpos(application: Application, context: Activity, emvEvents: EMVEvents) {
        qposApplication = application

        // check for bluetooth and location permission

        bluetoothPermissionHelper(context, context)

        pos = QPOSService.getInstance(context, QPOSService.CommunicationMode.BLUETOOTH)

        pos.setConext(context)
        TRACE.init(context)
        //init handler
        handler = Looper.myLooper()?.let { Handler(it) }

        if (handler != null) {
            pos.initListener(handler, QposListenerHelper(pos, emvEvents, context))
        }

    }

    fun searchForDevices(context: Context){
        pos.scanQPos2Mode(context, 20)
    }

    fun connectBluetoothDevice(bluetoothAddress: String) {
        pos.connectBluetoothDevice(true, 25, bluetoothAddress)
    }

    /**
     * The amount here should be minor amount
     **/
    fun setTransactionParam(amount: String, currencyCode: String) {
        PaybleConstants.currencyCode = currencyCode
        PaybleConstants.transAmount = amount
    }

    fun resetPosSdk() {
        pos.resetPosStatus()
    }

    fun startTrade() {
        try {
            val keyIndex = 0
            pos.setFormatId(QPOSService.FORMATID.MKSK_PLAIN)
            pos.setCardTradeMode(CardTradeMode.SWIPE_TAP_INSERT_CARD)
            pos.doTrade(keyIndex)
        } catch (e: Exception) {
           e.printStackTrace()
        }
    }

    fun bluetoothPermissionHelper(context: Context, activity: Activity) {
        var lm : LocationManager? = null
        val adapter = BluetoothAdapter.getDefaultAdapter()
        if (adapter != null && !adapter.isEnabled) { //if bluetooth is disabled, add one fix
            val enabler = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            context.startActivity(enabler)
        }
        lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val ok: Boolean = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (ok) { //Location service is on
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission denied
                // Request authorization
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_SCAN
                        ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_ADVERTISE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        val list = arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_ADVERTISE
                        )
                        ActivityCompat.requestPermissions(activity, list, BLUETOOTH_CODE)
                    }
                } else {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf<String>(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ),
                        LOCATION_CODE
                    )
                }
                //                        Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_SCAN
                        ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_ADVERTISE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        val list = arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_ADVERTISE
                        )
                        ActivityCompat.requestPermissions(activity, list, BLUETOOTH_CODE)
                    }
                }
                //                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(
                context,
                "System detects that the GPS location service is not turned on",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent()
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            try {
//                val launcher: ActivityResultLauncher<Intent> =
//                    registerForActivityResult<Intent, ActivityResult>(
//                        StartActivityForResult(),
//                        object : ActivityResultCallback<ActivityResult?> {
//                            override fun onActivityResult(result: ActivityResult) {}
//                        })
//                launcher.launch(intent)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)  // Ensure the settings activity is launched in a new task
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Pls open the LOCATION in your device settings!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    var deviceModel = Build.MODEL



    companion object {
        // The single instance of the class
        @Volatile private var instance: QposInitializer? = null

        fun getInstance(): QposInitializer {
            // Double-checked locking for thread safety
            return instance ?: synchronized(this) {
                instance ?: QposInitializer().also { instance = it }
            }
        }
    }
}