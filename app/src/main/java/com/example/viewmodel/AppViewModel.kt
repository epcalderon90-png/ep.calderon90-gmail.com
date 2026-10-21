package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppViewModel(application: Application, val repository: DataRepository) : AndroidViewModel(application) {

    // Active testing user
    val currentUserId = "vecino_cruceno"

    // Search and filter states
    private val _selectedCategory = MutableStateFlow("ALL") // ALL, URGENT, FREE, CHEAP
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedNeighborhood = MutableStateFlow("TODOS") // TODOS, or specific neighborhood
    val selectedNeighborhood: StateFlow<String> = _selectedNeighborhood.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Database states
    val currentUser: StateFlow<UserEntity?> = repository.getUserFlow(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allListings: StateFlow<List<ItemListingEntity>> = repository.getAllListings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNeighborhoods: StateFlow<List<NeighborhoodEntity>> = repository.getAllNeighborhoods()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered listings derived efficiently in Compose or ViewModel using combine
    val filteredListings: StateFlow<List<ItemListingEntity>> = combine(
        allListings, _selectedCategory, _selectedNeighborhood, _searchQuery
    ) { listings, category, neighborhood, query ->
        listings.filter { item ->
            // Filter out claimed items unless viewed in my postings count or administrative panel
            val statusMatches = item.moderationStatus == "APPROVED"
            
            val categoryMatches = category == "ALL" || item.category == category
            val neighborhoodMatches = neighborhood == "TODOS" || item.neighborhood.equals(neighborhood, ignoreCase = true)
            val queryMatches = query.isEmpty() || 
                    item.title.contains(query, ignoreCase = true) || 
                    item.description.contains(query, ignoreCase = true)
            
            statusMatches && categoryMatches && neighborhoodMatches && queryMatches
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Listing moderation list for Admin Dashboard
    val adminPendingListings: StateFlow<List<ItemListingEntity>> = allListings
        .map { list -> list.filter { it.moderationStatus == "PENDING" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // My active postings (claimed or active)
    val myPostings: StateFlow<List<ItemListingEntity>> = allListings
        .map { list -> list.filter { it.donorId == currentUserId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Messages
    val allMessages: StateFlow<List<MessageEntity>> = repository.getAllMessagesForUser(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Group active chats for inbox
    val inboxChats: StateFlow<List<InboxChatItem>> = allMessages
        .map { messages ->
            messages.groupBy { it.chatId }.map { (chatId, chatMsgs) ->
                val lastMsg = chatMsgs.first() // first is latest due to DESC order in query
                
                // Identify the other participant
                var otherId = lastMsg.senderId
                var otherName = lastMsg.senderName
                if (otherId == currentUserId) {
                    otherId = lastMsg.receiverId
                    otherName = if (otherId == "SYSTEM") "Pásalo SCZ" else "Vecino Local"
                }

                InboxChatItem(
                    chatId = chatId,
                    itemId = lastMsg.itemId,
                    itemTitle = lastMsg.itemTitle,
                    otherUserId = otherId,
                    otherUserName = otherName,
                    lastMessage = lastMsg.text,
                    timestamp = lastMsg.timestamp
                )
            }.sortedByDescending { it.timestamp }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active reviews received
    val myReviews: StateFlow<List<ReviewEntity>> = repository.getReviewsForUser(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin toggle
    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    init {
        viewModelScope.launch {
            // Seed sample database data
            repository.populateSeedData()
            
            // Check if current user exists, if not, create initial profile
            val exists = repository.getUser(currentUserId)
            if (exists == null) {
                val newUser = UserEntity(
                    id = currentUserId,
                    name = "Ronald Justiniano",
                    email = "ronald_scz@gmail.com",
                    phone = "75098765",
                    neighborhood = "Urbarí",
                    lat = -17.7942, lng = -63.1992,
                    tiluchis = 10, // starts with 10 Tiluchis
                    isVerified = false, // Verification required (DNI, GPS, Tel, Social)
                    reputation = 5.0f,
                    reviewsCount = 0
                )
                repository.saveUser(newUser)
                
                // Add an welcome alert systemic chat message
                repository.sendMessage(
                    MessageEntity(
                        chatId = "welcome_scz",
                        itemId = -1,
                        itemTitle = "Bienvenida",
                        senderId = "SYSTEM",
                        senderName = "Pásalo SCZ",
                        receiverId = currentUserId,
                        text = "¡Ujupá camba! Bienvenido a Pásalo SCZ 🦉, el app para regalar e intercambiar objetos gratis en Santa Cruz de la Sierra. Te regalamos un saldo inicial de 10 Tiluchis para empezar. ¡Completa tu verificación para ganar 30 Tiluchis extra y gozar de plena confianza comunitaria!"
                    )
                )
            }
        }
    }

    // Setters for search queries and fields
    fun setCategoryFilter(category: String) {
        _selectedCategory.value = category
    }

    fun setNeighborhoodFilter(hood: String) {
        _selectedNeighborhood.value = hood
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleAdminMode() {
        _isAdminMode.value = !_isAdminMode.value
    }

    // Business Logic Actions
    fun requestListingDetails(itemId: Int): Flow<ItemListingEntity?> {
        return repository.getListingById(itemId)
    }

    fun getChatMessages(chatId: String): Flow<List<MessageEntity>> {
        return repository.getMessagesForChat(chatId)
    }

    fun sendChatMessage(chatId: String, itemId: Int, itemTitle: String, receiverId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val msg = MessageEntity(
                chatId = chatId,
                itemId = itemId,
                itemTitle = itemTitle,
                senderId = currentUserId,
                senderName = user.name,
                receiverId = receiverId,
                text = text
            )
            repository.sendMessage(msg)
        }
    }

    fun donateItem(
        title: String,
        description: String,
        category: String, // URGENT, FREE, CHEAP
        priceInBs: Double,
        tiluchisCost: Int, // e.g. 1, 2, or 3 Tiluchis
        neighborhood: String,
        imageUrl: String = ""
    ) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            
            // Standard user listings are set as PENDING first for Admin review, while verified older listings are auto-approved.
            // Let's set it as APPROVED directly or PENDING depending on verification. Or APPROVED by default and show moderating controls.
            // To make it easy to demonstrate the admin screen, we can set any new listing to PENDING, allowing the user to switch to Admin view to Approve it! This is an amazing interactive flow.
            val item = ItemListingEntity(
                title = title,
                description = description,
                category = category,
                priceInBs = if (category == "CHEAP") priceInBs else 0.0,
                tiluchisCost = if (category == "URGENT") 0 else tiluchisCost,
                neighborhood = neighborhood,
                imageUrl = imageUrl,
                donorId = currentUserId,
                donorName = user.name,
                donorReputation = user.reputation,
                moderationStatus = "PENDING", // interactive approval flow
                isModerated = false
            )
            repository.saveListing(item)
        }
    }

    fun claimListingItem(item: ItemListingEntity, onResult: (ClaimResult) -> Unit) {
        viewModelScope.launch {
            val res = repository.claimItem(item, currentUserId)
            onResult(res)
        }
    }

    // Multi-step identity and community validation simulation/pipeline
    fun verifyIdentity(
        phone: String,
        ciNumber: String,
        neighborhood: String,
        socialHandle: String
    ) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            
            // Simulate rigorous verification, give them 30 Tiluchis index bonus!
            val updatedUser = user.copy(
                phone = phone,
                neighborhood = neighborhood,
                isVerified = true,
                verifiedId = true,
                verifiedLocation = true,
                verifiedPhone = true,
                verifiedSocial = true,
                tiluchis = user.tiluchis + 30 // Bonus tiluchi points to incentivize identity verification!
            )
            repository.saveUser(updatedUser)
            
            // Send automated verification success message
            repository.sendMessage(
                MessageEntity(
                    chatId = "welcome_scz",
                    itemId = -1,
                    itemTitle = "Verificación Exitosa",
                    senderId = "SYSTEM",
                    senderName = "Pásalo SCZ",
                    receiverId = currentUserId,
                    text = "🦉 ¡Verificado con éxito! Tu cuenta ha sido validada vía DNI/CI, Redes Sociales en el barrio de $neighborhood. Se te han sumado +30 Tiluchis extra de obsequio. ¡Disfruta compartiendo con tus vecinos de forma limpia y transparente!"
                )
            )
        }
    }

    // Purchase premium subscription hook ($5/month or 35 Bs for limitless out-of-neighborhood interactions)
    fun subscribePremium() {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val updatedUser = user.copy(
                isPremium = true,
                interactionsCount = 0 // unlimited
            )
            repository.saveUser(updatedUser)
            
            // Message alert
            repository.sendMessage(
                MessageEntity(
                    chatId = "welcome_scz",
                    itemId = -1,
                    itemTitle = "Suscripción Premium Activa",
                    senderId = "SYSTEM",
                    senderName = "Pásalo SCZ",
                    receiverId = currentUserId,
                    text = "✨ ¡Felicidades! Tu membresía 'Pásalo Premium SCZ' de 35 Bs./mes está activa. Ya puedes interactuar sin ningún tipo de límite con vecinos en todo Santa Cruz de la Sierra: Equipetrol, Urbarí, Plan 3000, Norte, Sur, etc."
                )
            )
        }
    }

    // Admin commands
    fun moderateApproveListing(itemId: Int) {
        viewModelScope.launch {
            val item = repository.getListingById(itemId).firstOrNull() ?: return@launch
            repository.updateListing(item.copy(moderationStatus = "APPROVED", isModerated = true))
        }
    }

    fun moderateRejectListing(itemId: Int) {
        viewModelScope.launch {
            val item = repository.getListingById(itemId).firstOrNull() ?: return@launch
            repository.updateListing(item.copy(moderationStatus = "REJECTED", isModerated = true))
        }
    }

    // Neighborhoods administration
    fun addNeighborhood(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.saveNeighborhood(NeighborhoodEntity(name))
        }
    }

    fun removeNeighborhood(name: String) {
        viewModelScope.launch {
            repository.deleteNeighborhood(name)
        }
    }

    // Profiles administration (Delete fake profiles control)
    fun deleteUserProfile(userId: String) {
        viewModelScope.launch {
            repository.deleteUser(userId)
        }
    }

    // Submit review & rating
    fun submitReview(itemId: Int, reviewedUserId: String, rating: Float, comment: String) {
        viewModelScope.launch {
            val reviewer = currentUser.value ?: return@launch
            val review = ReviewEntity(
                itemId = itemId,
                reviewerId = currentUserId,
                reviewerName = reviewer.name,
                reviewedUserId = reviewedUserId,
                rating = rating,
                comment = comment
            )
            repository.addReview(review)
        }
    }
}

// Data class for convenient message groupings in user inbox
data class InboxChatItem(
    val chatId: String,
    val itemId: Int,
    val itemTitle: String,
    val otherUserId: String,
    val otherUserName: String,
    val lastMessage: String,
    val timestamp: Long
)

// Factory
class AppViewModelFactory(
    private val application: Application,
    private val repository: DataRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
