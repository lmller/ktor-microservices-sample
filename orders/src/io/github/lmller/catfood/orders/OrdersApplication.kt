package io.github.lmller.catfood.orders

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.lmller.catfood.orders.warehouse.WarehouseService
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.routing
import io.ktor.sessions.*
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.getOrFail
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI // used for getOrFail()
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

  val finishedOrders = mutableListOf<List<Item>>()
  val warehouseService = WarehouseService(this)

  installContentNegotiation()
  installSessions()

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
      if (session.shoppingCart.isNotEmpty()) {

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
      if (order != null) {
        val status = if (index % 2 == 0) "Delivered" else "Picking"
        call.respond(HttpStatusCode.OK, OrderStatus(order, status))
      } else {
        call.respond(HttpStatusCode.NotFound)
      }
    }
  }
}

private fun ApplicationCall.getSession() = this.sessions.get<UserSession>() ?: UserSession(
  UUID.randomUUID().toString(),
  emptyList()
)

