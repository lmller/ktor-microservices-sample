package io.github.lmller.catfood.configurator.warehouse

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.content.TextContent
import io.ktor.http.ContentType

object JsonContent {
    private val mapper = jacksonObjectMapper()
    operator fun invoke(json: Any): TextContent {
        return TextContent(
            mapper.writeValueAsString(json),
            contentType = ContentType.Application.Json
        )
    }
}