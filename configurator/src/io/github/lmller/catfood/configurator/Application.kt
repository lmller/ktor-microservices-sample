package io.github.lmller.catfood.configurator

import freemarker.cache.ClassTemplateLoader
import io.github.lmller.catfood.configurator.orders.OrdersService
import io.github.lmller.catfood.configurator.orders.ShoppingCartService
import io.github.lmller.catfood.configurator.warehouse.WarehouseService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.freemarker.FreeMarker
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    val warehouseService = WarehouseService(this)
    val cartService = ShoppingCartService(this)
    val orderService = OrdersService(this)
    val messages = mutableListOf<String>()

    routing {
        get("/") {
            val items = warehouseService.getStock()
            val cart = cartService.getShoppingCart()

            call.respond(
                FreeMarkerContent(
                    "index.ftl",
                    mapOf("items" to items, "cart" to cart, "messages" to messages)
                )
            )

            messages.clear()
        }

        post("/item") {
            val itemName = call.receiveParameters()["itemName"]
            val item = warehouseService.find { it.name == itemName }
            if (item != null) {
                cartService.addToCart(item)
            }

            call.respondRedirect("/")
        }

        post("/checkout") {
            messages.add(orderService.placeOrder())

            call.respondRedirect("/")
        }
    }
}

data class Item(val name: String, val price: Double, val quantity: Int? = null)
data class Cart(val items: List<Item>) {
    val totalAmount
        get() = items.sumByDouble { it.price }
}

