package com.lovisgod.payble_qpos_sdk.network

import com.lovisgod.payble_qpos_sdk.network.models.AgentData
import com.lovisgod.payble_qpos_sdk.network.models.TerminalKeyResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("agent/agents/{id}")
    fun getAgentDetails(@Path("id") id: String,
                        @Header("api_key") api_key: String,
                        @Header("merchant_id") mid: String): Call<AgentData>

    @GET("https://trans-middleware-31e82172a3e1.herokuapp.com/get-local-keys")
    fun getKeys(@Query("terminalId") terminalId: String): Call<TerminalKeyResponse>
}
