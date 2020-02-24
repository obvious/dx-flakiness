package com.vinaysshenoy.quarantine

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface QuarantineApi {

    @POST("{projectSlug}/reports")
    fun sendTestRun(
        @Path("projectSlug") projectSlug: String,
        @Body payload: List<TestDescriptor>
    ): Call<String>
}