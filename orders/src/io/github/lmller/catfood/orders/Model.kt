package io.github.lmller.catfood.orders

import java.math.BigDecimal

data class UserSession(val name: String, val shoppingCart: List<Item>)

data class Item(val name: String, val price: BigDecimal = BigDecimal.ZERO, val quantity: Int)

data class Stock(val name: String, val quantity: Int)

data class OrderStatus(val items: List<Item>, val status: String)

