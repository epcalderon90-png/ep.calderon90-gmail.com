package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "neighborhoods")
data class NeighborhoodEntity(
    @PrimaryKey val name: String
)
