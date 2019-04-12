package io.github.lmller.catfood.configurator.warehouse

import io.github.lmller.catfood.configurator.Item

import io.ktor.application.Application
import io.ktor.client.request.get

import io.ktor.util.KtorExperimentalAPI
import kotlin.random.Random

@KtorExperimentalAPI
class WarehouseService(application: Application) {
    private val prices = mutableMapOf<String, Double>()
    private val url by lazy {
        application.environment.config.property("warehouse.url").getString()
    }

    suspend fun getStock(): List<Item> {
        return httpClient.get<List<StockDto>>(url).map {
            Item(
                it.name,
                priceFor(it.name)
            )
        }
    }

    suspend fun find(predicate: (Item) -> Boolean): Item? {
        return getStock().find(predicate)
    }

    private fun priceFor(name: String) = prices.computeIfAbsent(name) { Random.nextInt(1000) / 100.0 }
}


private data class StockDto(val name: String, val quantity: Int)


