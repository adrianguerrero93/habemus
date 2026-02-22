package ar.habemosbar.sales.data.local.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SimpleProductDatabase(context: Context) : SQLiteOpenHelper(context, "habemus_sales.db", null, 1) {
    
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE products (
                id INTEGER PRIMARY KEY,
                nombre TEXT NOT NULL,
                precioConsumidorFinal REAL NOT NULL,
                precioComercio REAL NOT NULL
            )
        """)
        // Seed initial data
        seedInitialData(db)
    }
    
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS products")
        onCreate(db)
    }
    
    private fun seedInitialData(db: SQLiteDatabase) {
        val products = ProductSeed.products
        for ((idStr, nameAndPrices) in products) {
            val (name, prices) = nameAndPrices
            val (priceConsumerFinal, priceComercio) = prices
            db.execSQL("""
                INSERT INTO products (id, nombre, precioConsumidorFinal, precioComercio)
                VALUES (${idStr.toInt()}, '$name', $priceConsumerFinal, $priceComercio)
            """)
        }
    }
    
    fun getAllProducts(): List<ProductEntity> {
        val db = readableDatabase
        val cursor = db.query("products", null, null, null, null, null, null)
        val products = mutableListOf<ProductEntity>()
        
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val precioConsumidorFinal = cursor.getDouble(cursor.getColumnIndexOrThrow("precioConsumidorFinal"))
            val precioComercio = cursor.getDouble(cursor.getColumnIndexOrThrow("precioComercio"))
            
            products.add(ProductEntity(id, nombre, precioConsumidorFinal, precioComercio))
        }
        cursor.close()
        return products
    }
}
