package io.github.lmller.catfood.orders

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.sessions.SessionSerializer
import io.ktor.sessions.SessionStorageMemory
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie

fun Application.installSessions() {
  install(Sessions) {
    cookie<UserSession>("SHOPPING_CART", storage = SessionStorageMemory()) {
      cookie.path = "/"
      serializer = jacksonSessionSerializer()
    }
  }
}

fun Application.installContentNegotiation() {
  install(ContentNegotiation) {
    jackson {
      enable(SerializationFeature.INDENT_OUTPUT)
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