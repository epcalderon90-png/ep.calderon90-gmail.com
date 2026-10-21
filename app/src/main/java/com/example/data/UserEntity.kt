package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val neighborhood: String, // e.g. "Equipetrol", "Urbarí", "Plan 3000", "Villa 1ro de Mayo", "La Morita"
    val lat: Double = -17.7833, // Santa Cruz central coordinates by default
    val lng: Double = -63.1821,
    val tiluchis: Int = 50, // default "tiluchis" currency of exchange instead of Geev bananas
    val isVerified: Boolean = false,
    val verifiedId: Boolean = false,
    val verifiedLocation: Boolean = false,
    val verifiedPhone: Boolean = false,
    val verifiedSocial: Boolean = false,
    val isPremium: Boolean = false,
    val interactionsCount: Int = 0, // premium allows unlimited; standard limited to 5 inter-neighborhood interactions
    val reputation: Float = 5.0f,
    val reviewsCount: Int = 0
)
