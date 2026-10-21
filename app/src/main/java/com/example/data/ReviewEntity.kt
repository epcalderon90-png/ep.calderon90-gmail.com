package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemId: Int,
    val reviewerId: String,
    val reviewerName: String,
    val reviewedUserId: String,
    val rating: Float,
    val comment: String,
    val timestamp: Long = System.currentTimeMillis()
)
