package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "listings")
data class ItemListingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String, // "URGENT", "FREE", "CHEAP"
    val priceInBs: Double = 0.0, // for Cheap Offer
    val tiluchisCost: Int = 1, // virtual currency required to obtain/exchange this item (e.g. 1 tiluchi, 0 for urgent)
    val neighborhood: String,
    val imageUrl: String = "", // empty or styling resource
    val donorId: String,
    val donorName: String,
    val donorReputation: Float = 5.0f,
    val timestamp: Long = System.currentTimeMillis(),
    val isModerated: Boolean = false,
    val moderationStatus: String = "APPROVED" // "APPROVED", "PENDING", "REJECTED"
)
