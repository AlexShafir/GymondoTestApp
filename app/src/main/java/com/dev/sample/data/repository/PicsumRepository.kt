package com.dev.sample.data.repository

import com.dev.sample.data.remote.PicsumApi
import com.dev.sample.data.remote.PicsumItem
import java.io.IOException
import javax.inject.Inject

interface IPicsumRepository {
    suspend fun getDefaultImages(): List<PicsumItem>
}

class PicsumRepository @Inject constructor(
    private val picsumApi: PicsumApi
): IPicsumRepository {

    override suspend fun getDefaultImages(): List<PicsumItem> {

        val res = try {
            picsumApi.getDefaultImages()
        } catch (_: IOException) {
            throw ServerUnavailable()
        }

        val code = res.code()
        return if (code == 200) {
            res.body()!!
        } else {
            throw ServerError(code)
        }
    }

}