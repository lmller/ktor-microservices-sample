package io.github.lmller.catfood.configurator.orders

import io.github.lmller.catfood.configurator.warehouse.JsonContent
import io.github.lmller.catfood.configurator.warehouse.httpClient
import io.ktor.application.Application
import io.ktor.client.call.receive
import io.ktor.client.request.post
import io.ktor.client.response.HttpResponse
import io.ktor.client.response.readText
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
class OrdersService(application: Application) {

    private val ordersUrl by lazy {
        application.environment.config.property("orders.ordersUrl").getString()
    }

    suspend fun placeOrder(): String {
        val response = httpClient.post<HttpResponse>(ordersUrl) {
            body = JsonContent("")
        }

        return response.call.receive()
    }
}
