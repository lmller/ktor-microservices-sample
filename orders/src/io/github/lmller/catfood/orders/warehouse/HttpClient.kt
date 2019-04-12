package io.github.lmller.catfood.orders.warehouse


import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import okhttp3.Credentials


val httpClient by lazy {
    HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
        engine {
            config {
                authenticator { route, response ->
                    val credentials = Credentials.basic("order-service", "supersecure!")
                    response.request().newBuilder().addHeader("Authorization", credentials).build()
                }

            }

        }
    }
}