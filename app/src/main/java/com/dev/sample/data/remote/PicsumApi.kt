package com.dev.sample.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface PicsumApi {

    @GET("v2/list")
    suspend fun getDefaultImages(): Response<List<PicsumItem>>

}

data class PicsumItem(
    val id: String,
    val author: String,
    val download_url: String
)