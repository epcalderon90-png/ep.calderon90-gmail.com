package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.random.Random

class DataRepository(private val appDao: AppDao) {

    // Users
    fun getUserFlow(userId: String): Flow<UserEntity?> = appDao.getUserByIdFlow(userId)
    suspend fun getUser(userId: String): UserEntity? = appDao.getUserById(userId)
    fun getAllUsers(): Flow<List<UserEntity>> = appDao.getAllUsersFlow()
    suspend fun saveUser(user: UserEntity) = appDao.insertUser(user)
    suspend fun updateUser(user: UserEntity) = appDao.updateUser(user)
    suspend fun deleteUser(userId: String) = appDao.deleteUserById(userId)

    // Neighborhoods
    fun getAllNeighborhoods(): Flow<List<NeighborhoodEntity>> = appDao.getAllNeighborhoodsFlow()
    suspend fun saveNeighborhood(neighborhood: NeighborhoodEntity) = appDao.insertNeighborhood(neighborhood)
    suspend fun deleteNeighborhood(name: String) = appDao.deleteNeighborhood(name)

    // Listings
    fun getAllListings(): Flow<List<ItemListingEntity>> = appDao.getAllListingsFlow()
    fun getListingById(id: Int): Flow<ItemListingEntity?> = appDao.getListingByIdFlow(id)
    suspend fun saveListing(listing: ItemListingEntity) = appDao.insertListing(listing)
    suspend fun deleteListing(id: Int) = appDao.deleteListingById(id)
    suspend fun updateListing(listing: ItemListingEntity) = appDao.updateListing(listing)

    // Messages
    fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>> = appDao.getMessagesForChatFlow(chatId)
    fun getAllMessagesForUser(userId: String): Flow<List<MessageEntity>> = appDao.getAllMessagesForUserFlow(userId)
    suspend fun sendMessage(message: MessageEntity) = appDao.insertMessage(message)

    // Reviews
    fun getReviewsForUser(userId: String): Flow<List<ReviewEntity>> = appDao.getReviewsForUserFlow(userId)
    suspend fun addReview(review: ReviewEntity) {
        appDao.insertReview(review)
        
        // Recalculate average reputation for reviewed user
        val userReviews = appDao.getReviewsForUserFlow(review.reviewedUserId).firstOrNull() ?: emptyList()
        val allReviews = userReviews + review
        val averageRating = allReviews.map { it.rating }.average().toFloat()
        
        val user = appDao.getUserById(review.reviewedUserId)
        if (user != null) {
            appDao.updateUser(user.copy(
                reputation = averageRating,
                reviewsCount = allReviews.size
            ))
        }
    }

    // Community and verification transaction flow
    suspend fun claimItem(item: ItemListingEntity, claimerId: String): ClaimResult {
        val claimer = appDao.getUserById(claimerId) ?: return ClaimResult.Error("Usuario no encontrado")
        val donor = appDao.getUserById(item.donorId) ?: return ClaimResult.Error("Ofertante no encontrado")

        // 1. Verification requirement
        if (!claimer.isVerified) {
            return ClaimResult.Error("Debes verificar tu perfil primero (Identificación, Teléfono y Ubicación) en la pestaña Mi Perfil.")
        }

        // 2. Neighborhood check & Subscriptions (limiting non-premium interactions out of neighbourhood to 5)
        val sameNeighborhood = claimer.neighborhood.equals(item.neighborhood, ignoreCase = true)
        if (!sameNeighborhood) {
            if (!claimer.isPremium) {
                if (claimer.interactionsCount >= 5) {
                    return ClaimResult.RequirePremium("Has alcanzado tu límite de 5 interacciones gratuitas fuera de tu barrio (${claimer.neighborhood}). ¡Adquiere Pásalo Premium para interacciones ilimitadas entre barrios!")
                } else {
                    // Count interaction
                    appDao.updateUser(claimer.copy(interactionsCount = claimer.interactionsCount + 1))
                }
            }
        }

        // 3. Virtual currency checkout (Tiluchis check)
        if (item.category != "URGENT") { // Urgent donations are free (0 Tiluchis)
            if (claimer.tiluchis < item.tiluchisCost) {
                return ClaimResult.Error("No tienes suficientes Tiluchis 🦉. Consigue más donando objetos a la comunidad o completando verificaciones.")
            }
            
            // Deduct from claimer, give to donor (incentivizing recycling!)
            appDao.updateUser(claimer.copy(tiluchis = claimer.tiluchis - item.tiluchisCost))
            appDao.updateUser(donor.copy(tiluchis = donor.tiluchis + item.tiluchisCost))
        }

        // 4. Update listing moderation/status on claim
        appDao.insertListing(item.copy(moderationStatus = "CLAIMED"))

        // 5. Send an automated chat confirmation message
        val automatedChatId = "${item.id}_${claimer.id}_${item.donorId}"
        val automatedMsg = MessageEntity(
            chatId = automatedChatId,
            itemId = item.id,
            itemTitle = item.title,
            senderId = "SYSTEM",
            senderName = "Pásalo SCZ",
            receiverId = item.donorId,
            text = "¡Hola! Estoy muy interesado en tu objeto '${item.title}' de la categoría ${getCategoryLabel(item.category)}. Mi barrio es ${claimer.neighborhood}. ¿Podemos coordinar la entrega? ¡Gracias! (Intercambio asegurado por ${item.tiluchisCost} Tiluchis 🦉)"
        )
        appDao.insertMessage(automatedMsg)

        return ClaimResult.Success(automatedChatId)
    }

    private fun getCategoryLabel(cat: String): String = when(cat) {
        "URGENT" -> "⚠️ Donación Urgente"
        "FREE" -> "🎁 Regalo Común"
        "CHEAP" -> "🏷️ Baratísimo"
        else -> "Intercambio"
    }

    // Call this on launch to generate active seed listings if database is empty
    suspend fun populateSeedData() {
        // Seed default neighborhoods if none exist in Room
        val defaultNeighborhoods = listOf(
            "Equipetrol", "Urbarí", "Plan 3000", "Villa Primero de Mayo", "La Morita", "Centro"
        )
        val currentNeighborhoods = appDao.getAllNeighborhoodsFlow().firstOrNull() ?: emptyList()
        if (currentNeighborhoods.isEmpty()) {
            for (name in defaultNeighborhoods) {
                appDao.insertNeighborhood(NeighborhoodEntity(name))
            }
        }

        // Build mock donor profiles in SCZ
        val seeds = listOf(
            UserEntity(
                id = "donor_juan",
                name = "Juan Pablo Vargas",
                email = "juan.pv@gmail.com",
                phone = "78012345",
                neighborhood = "Equipetrol",
                lat = -17.7681, lng = -63.1945,
                tiluchis = 30,
                isVerified = true,
                verifiedId = true, verifiedLocation = true, verifiedPhone = true, verifiedSocial = true,
                reputation = 4.8f, reviewsCount = 12
            ),
            UserEntity(
                id = "donor_mariela",
                name = "Mariela Pinto",
                email = "mariela.p@gmail.com",
                phone = "69087654",
                neighborhood = "Urbarí",
                lat = -17.7942, lng = -63.1992,
                tiluchis = 22,
                isVerified = true,
                verifiedId = true, verifiedLocation = true, verifiedPhone = true, verifiedSocial = true,
                reputation = 4.9f, reviewsCount = 8
            ),
            UserEntity(
                id = "donor_carlos",
                name = "Carlos Justiniano",
                email = "carlos.juz@hotmail.com",
                phone = "71544332",
                neighborhood = "Plan 3000",
                lat = -17.8285, lng = -63.1328,
                tiluchis = 15,
                isVerified = true,
                verifiedId = true, verifiedLocation = true, verifiedPhone = true, verifiedSocial = false,
                reputation = 4.5f, reviewsCount = 5
            ),
            UserEntity(
                id = "donor_sandra",
                name = "Sandra Aguilera",
                email = "sandra_scz@gmail.com",
                phone = "70899881",
                neighborhood = "Villa Primero de Mayo",
                lat = -17.7885, lng = -63.1182,
                tiluchis = 40,
                isVerified = true,
                verifiedId = true, verifiedLocation = true, verifiedPhone = true, verifiedSocial = true,
                reputation = 4.7f, reviewsCount = 9
            )
        )

        for (u in seeds) {
            if (appDao.getUserById(u.id) == null) {
                appDao.insertUser(u)
            }
        }

        // Build core mock items
        val list = listOf(
            ItemListingEntity(
                title = "Cocina de gas de 4 hornallas",
                description = "Funciona perfecto, solo está sucia. Ideal para alguien que realmente la necesite con urgencia para cocinar.",
                category = "URGENT",
                tiluchisCost = 0, // Urgent donations are free!
                neighborhood = "Plan 3000",
                donorId = "donor_carlos",
                donorName = "Carlos Justiniano",
                donorReputation = 4.5f,
                moderationStatus = "APPROVED",
                isModerated = true
            ),
            ItemListingEntity(
                title = "Pañales para recién nacido Pampers (60 u)",
                description = "Donación urgente para alguna mamá o papá soltero en situación vulnerable. Nos sobraron de mi primer bebé.",
                category = "URGENT",
                tiluchisCost = 0,
                neighborhood = "Villa Primero de Mayo",
                donorId = "donor_sandra",
                donorName = "Sandra Aguilera",
                donorReputation = 4.7f,
                moderationStatus = "APPROVED",
                isModerated = true
            ),
            ItemListingEntity(
                title = "Estante de madera para oficina",
                description = "Estante mediano, color café tabaco. En buen estado, solo tiene unos raspones de transporte mudanza.",
                category = "FREE",
                tiluchisCost = 2, // Standard exchanges require 2 Tiluchis
                neighborhood = "Equipetrol",
                donorId = "donor_juan",
                donorName = "Juan Pablo Vargas",
                donorReputation = 4.8f,
                moderationStatus = "APPROVED",
                isModerated = true
            ),
            ItemListingEntity(
                title = "Silla de comer para bebé - Graco",
                description = "Silla de comer plegable en material plástico grueso. Muy segura, lavable.",
                category = "FREE",
                tiluchisCost = 1,
                neighborhood = "Urbarí",
                donorId = "donor_mariela",
                donorName = "Mariela Pinto",
                donorReputation = 4.9f,
                moderationStatus = "APPROVED",
                isModerated = true
            ),
            ItemListingEntity(
                title = "Licuadora Oster antigua Baratísima",
                description = "Licuadora marca Oster original, vaso de vidrio grueso. Funciona con mucha fuerza, vendo baratísimo a solo 20 Bs.",
                category = "CHEAP",
                priceInBs = 20.0,
                tiluchisCost = 1,
                neighborhood = "Equipetrol",
                donorId = "donor_juan",
                donorName = "Juan Pablo Vargas",
                donorReputation = 4.8f,
                moderationStatus = "APPROVED",
                isModerated = true
            ),
            ItemListingEntity(
                title = "Cuna de viaje desarmable",
                description = "Cuna portátil plegable. Pequeño hueco en la malla lateral pero fácil de reparar. La vendo barata para que ruede rápido, a solo 35 Bs.",
                category = "CHEAP",
                priceInBs = 35.0,
                tiluchisCost = 1,
                neighborhood = "Urbarí",
                donorId = "donor_mariela",
                donorName = "Mariela Pinto",
                donorReputation = 4.9f,
                moderationStatus = "APPROVED",
                isModerated = true
            )
        )

        // Only insert if no entries currently exist in listings
        val currentListings = appDao.getAllListingsFlow().firstOrNull() ?: emptyList()
        if (currentListings.isEmpty()) {
            for (item in list) {
                appDao.insertListing(item)
            }
        }
    }
}

sealed interface ClaimResult {
    data class Success(val chatId: String) : ClaimResult
    data class Error(val message: String) : ClaimResult
    data class RequirePremium(val message: String) : ClaimResult
}
