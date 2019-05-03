package io.github.lmller.catfood.warehouse

import org.jetbrains.exposed.dao.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class InMemoryWarehouse : Warehouse {

    private val database = createDatabase()

    override val stock: WarehouseStock
        get() = transaction(database) {
            StockTable.selectAll()
                .map { it[StockTable.itemName] to it[StockTable.quantity] }
                .toMap()
        }

    override fun update(item: String, newQuantity: Int) {
        transaction {
            StockTable.update({ StockTable.itemName eq item }) {
                it[StockTable.quantity] = newQuantity
            }

        }
    }
}

private object StockTable : UUIDTable() {
    val itemName = varchar("itemName", 128).uniqueIndex()
    val quantity = integer("quantity")

    fun insertTestData() {
        insert { column ->
            column[itemName] = "Fish"
            column[quantity] = 100
        }
        insert { column ->
            column[itemName] = "Beef"
            column[quantity] = 100
        }
        insert { column ->
            column[itemName] = "Lamb"
            column[quantity] = 50
        }
    }
}

private fun createDatabase(): Database {
    //an h2 is obviously not the right choice for a production system ;-)
    val db = Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

    transaction(db) {
        SchemaUtils.create(StockTable)
        StockTable.insertTestData()
    }

    return db
}


