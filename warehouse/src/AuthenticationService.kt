package io.github.lmller.catfood.warehouse

fun isAuthenticated(user: String, pw: String): Boolean {
    return user == "order-service" && pw == "supersecure!"
}
