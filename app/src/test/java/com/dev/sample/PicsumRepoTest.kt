package com.dev.sample

import com.dev.sample.data.remote.PicsumApi
import com.dev.sample.data.remote.PicsumItem
import com.dev.sample.data.repository.PicsumRepository
import com.dev.sample.data.repository.ServerError
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows

class PicsumRepositoryTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var picsumApi: PicsumApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        picsumApi = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PicsumApi::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    /**
     * Test that server response is correctly parsed
     */
    @Test
    fun `repo_getDefaultImages returns response 200`() = runTest {
        val mockResponseText = """
                [
                    {
                        "id": "7",
                        "author": "Alejandro Escamilla",
                        "width": 4728,
                        "height": 3168,
                        "url": "https://unsplash.com/photos/BbQLHCpVUqA",
                        "download_url": "https://picsum.photos/id/7/4728/3168"
                    }
                ]
            """.trimIndent()

        val canonicalList = listOf(
            PicsumItem(
                "7", "Alejandro Escamilla", "https://picsum.photos/id/7/4728/3168"
            )
        )

        // Arrange
        val mockResponse = MockResponse()
            .setBody(mockResponseText)
            .setResponseCode(200)

        mockWebServer.enqueue(mockResponse)
        val repo = PicsumRepository(picsumApi)

        // Act
        val response = repo.getDefaultImages()

        assertEquals(response, canonicalList)
    }

    /**
     * Test that repository throws proper exception
     */
    @Test
    fun `repo_getDefaultImages throws exception`() = runTest {
        // Arrange
        val mockResponse = MockResponse()
            .setResponseCode(500)

        mockWebServer.enqueue(mockResponse)

        val repo = PicsumRepository(picsumApi)

        // Assert
        val exception = assertThrows(ServerError::class.java) { runBlocking { repo.getDefaultImages() } }
        assertEquals(500, exception.code)
    }
}