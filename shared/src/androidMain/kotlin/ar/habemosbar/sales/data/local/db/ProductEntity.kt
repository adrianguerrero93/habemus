package ar.habemosbar.sales.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val name: String,
    val priceConsumerFinal: Double,
    val priceRetail: Double
)
