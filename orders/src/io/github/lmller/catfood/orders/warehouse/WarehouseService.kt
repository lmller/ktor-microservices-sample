package io.github.lmller.catfood.orders.warehouse

import io.github.lmller.catfood.orders.Stock
import io.ktor.application.Application
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.features.NotFoundException
import io.ktor.util.KtorExperimentalAPI


@KtorExperimentalAPI
class WarehouseService(application: Application) {

    private val url by lazy {
        application.environment.config.property("warehouse.url").getString()
    }

    suspend fun getStock(): List<Stock> {
        return httpClient.get(url)
    }

    suspend fun reduceStock(itemName: String, itemsRemoved: Int) {
        val currentStock = getStock().find { it.name == itemName }
            ?: throw NotFoundException("Can't find stock item $itemName in warehouse!")

        httpClient.put<Any>("$url/$itemName") {
            body = JsonContent(Stock(itemName, currentStock.quantity - itemsRemoved))
        }
    }
}


