package com.vinaysshenoy.quarantine

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface QuarantineApi {

    @POST("quarantine")
    fun sendTestRun(@Body payload: List<TestDescriptor>): Call<String>
}