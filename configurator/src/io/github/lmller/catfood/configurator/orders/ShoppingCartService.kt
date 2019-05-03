package io.github.lmller.catfood.configurator.orders

import io.github.lmller.catfood.configurator.Cart
import io.github.lmller.catfood.configurator.Item
import io.github.lmller.catfood.configurator.warehouse.JsonContent
import io.github.lmller.catfood.configurator.warehouse.httpClient
import io.ktor.application.Application
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.response.HttpResponse
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
class ShoppingCartService(application: Application) {

    private val shoppingCartUrl by lazy {
        application.environment.config
            .property("orders.shoppingcartUrl").getString()
    }

    suspend fun getShoppingCart(): Cart {
        return Cart(httpClient.get(shoppingCartUrl))
    }

    suspend fun addToCart(item: Item) {
        httpClient.put<HttpResponse>(shoppingCartUrl) {
            body = JsonContent(item)
        }
    }
}
