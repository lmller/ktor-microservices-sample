package io.github.lmller.catfood.orders

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.lmller.catfood.orders.warehouse.WarehouseService
import io.ktor.jackson.*
import io.ktor.features.*
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie

import io.ktor.sessions.*
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.getOrFail
import io.ktor.util.pipeline.PipelineContext
import java.lang.RuntimeException
import java.util.*
import kotlin.random.Random

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI // used for getOrFail()
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    val finishedOrders = mutableListOf<List<Item>>()
    val warehouseService = WarehouseService(this)

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(Sessions) {
        // SessionStorageMemory is for development purposes only
        cookie<UserSession>("SHOPPING_CART", storage = SessionStorageMemory()) {
            cookie.path = "/"
            serializer = jacksonSessionSerializer()
        }

    }

    routing {
        put("/shoppingcart/items") {
            val session = call.getSession()

            val itemToAdd = call.receive<Item>()
            call.sessions.set(
                session.copy(shoppingCart = session.shoppingCart + itemToAdd)
            )

            call.respond(HttpStatusCode.Created, "Added ${itemToAdd.name} to cart.")
        }

        get("/shoppingcart/items") {
            val session = call.getSession()

            call.respond(session.shoppingCart)
        }

        post("/orders") {
            val session = call.getSession()
            if(session.shoppingCart.isNotEmpty()) {

                session.shoppingCart.forEach {
                    warehouseService.reduceStock(it.name, it.quantity)
                }

                finishedOrders.add(session.shoppingCart)
                call.sessions.clear<UserSession>()
                call.respond(HttpStatusCode.Created, "Successfully placed order ${finishedOrders.size}")
            } else {
                call.respond(HttpStatusCode.NotFound, "Shopping cart is empty.")
            }
        }

        get("/orders/{id}/status") {
            //in real life, we should check whether the user has the rights to get the status...
            val index = call.parameters.getOrFail("id").toInt()
            val order = finishedOrders.getOrNull(index - 1)
            if(order != null) {
                val status = if (index % 2 == 0) "Delivered" else "Picking"
                call.respond(HttpStatusCode.OK, OrderStatus(order, status))
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}

private fun jacksonSessionSerializer(): SessionSerializer {
    return object : SessionSerializer {
        val objectMapper = jacksonObjectMapper()
        override fun deserialize(text: String) = objectMapper.readValue(text, UserSession::class.java)

        override fun serialize(session: Any) = objectMapper.writeValueAsString(session)
    }
}

private fun ApplicationCall.getSession() = this.sessions.get<UserSession>() ?: UserSession(
    UUID.randomUUID().toString(),
    emptyList()
)

