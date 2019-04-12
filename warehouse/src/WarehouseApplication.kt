package io.github.lmller.catfood.warehouse

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.basic
import io.ktor.features.BadRequestException
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.jackson.jackson
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.put
import io.ktor.routing.routing
import io.ktor.server.netty.EngineMain
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.getOrFail

fun main(args: Array<String>): Unit = EngineMain.main(args)

@KtorExperimentalAPI // used for getOrFail
@Suppress("unused") // Referenced in application.conf
@JvmOverloads
fun Application.module(testing: Boolean = false) {
//  install(ContentNegotiation) {
//    jackson {
//      enable(SerializationFeature.INDENT_OUTPUT)
//    }
//  }

  install(Authentication) {
    basic {
      realm = "lmller.github.io"
      validate { (user, pw) ->
        if (isAuthenticated(user, pw)) UserIdPrincipal(user) else null
      }
    }
  }

  val warehouse = InMemoryWarehouse()

  routing {
    get("/stock") {

      call.respond(warehouse.stock.map { (k, v) -> StockDto(k, v) })
    }
    authenticate {
      put("/stock/{item}") {
        val itemName = call.parameters.getOrFail("item")
        val stock = call.receiveOrNull<StockDto>() ?: throw BadRequestException("Missing required body")
        warehouse.update(itemName, stock.quantity ?: 0)


        call.respond(OK)
      }
    }
  }
}

data class StockDto(val name: String?, val quantity: Int?)

interface Warehouse {
  val stock: WarehouseStock
  fun update(item: String, newQuantity: Int)
}

typealias WarehouseStock = Map<String, Int>


