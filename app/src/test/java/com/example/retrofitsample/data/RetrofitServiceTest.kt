package com.example.retrofitsample.data

import com.example.retrofitsample.data.model.RemoteResult
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.junit.Assert.*


class RetrofitServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: RetrofitService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/")) // Retrofit apunta al servidor falso
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `listImages devuelve RemoteResult cuando la llamada es exitosa (200 OK)`() = runTest {
        // 1) Respuesta simulada
        val mockJsonResponse = """
            [
              {
                "id": "MTY1ODc5MA",
                "url": "https://cdn2.thecatapi.com/images/MTY1ODc5MA.jpg",
                "width": 600,
                "height": 400
              }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockJsonResponse)
        )

        // 2) Ejecución
        val result: RemoteResult = service.listImages(apiKey = "test_api_key")

        // 3) Asserts (JUnit)
        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("MTY1ODc5MA", result[0].id)
        assertEquals("https://cdn2.thecatapi.com/images/MTY1ODc5MA.jpg", result[0].url)
        assertEquals(600, result[0].width)
        assertEquals(400, result[0].height)

        // 4) Verificar request
        val request = mockWebServer.takeRequest()
        val path = request.path ?: ""
        assertTrue(path.contains("images/search"))
        assertTrue(path.contains("api_key=test_api_key"))
        assertEquals("GET", request.method)
    }
}
