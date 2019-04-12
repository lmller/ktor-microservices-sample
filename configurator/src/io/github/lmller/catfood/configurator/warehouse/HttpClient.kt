package io.github.lmller.catfood.configurator.warehouse


import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.cookies.AcceptAllCookiesStorage
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import okhttp3.Credentials


val httpClient by lazy {
    HttpClient(OkHttp) {
        expectSuccess = false

        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
        install(HttpCookies) {
            // Will keep an in-memory map with all the cookies from previous requests.
            storage = AcceptAllCookiesStorage()
        }

    }
}