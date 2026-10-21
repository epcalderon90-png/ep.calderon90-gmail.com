package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Users
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserByIdFlow(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Update
    suspend fun updateUser(user: UserEntity)

    // Listings
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: ItemListingEntity)

    @Query("SELECT * FROM listings ORDER BY timestamp DESC")
    fun getAllListingsFlow(): Flow<List<ItemListingEntity>>

    @Query("SELECT * FROM listings WHERE id = :id")
    fun getListingByIdFlow(id: Int): Flow<ItemListingEntity?>

    @Query("SELECT * FROM listings WHERE id = :id")
    suspend fun getListingById(id: Int): ItemListingEntity?

    @Query("DELETE FROM listings WHERE id = :id")
    suspend fun deleteListingById(id: Int)

    @Update
    suspend fun updateListing(listing: ItemListingEntity)

    // Messages
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesForChatFlow(chatId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE senderId = :userId OR receiverId = :userId ORDER BY timestamp DESC")
    fun getAllMessagesForUserFlow(userId: String): Flow<List<MessageEntity>>

    // Reviews
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Query("SELECT * FROM reviews WHERE reviewedUserId = :userId ORDER BY timestamp DESC")
    fun getReviewsForUserFlow(userId: String): Flow<List<ReviewEntity>>

    // Neighborhoods
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNeighborhood(neighborhood: NeighborhoodEntity)

    @Query("SELECT * FROM neighborhoods ORDER BY name ASC")
    fun getAllNeighborhoodsFlow(): Flow<List<NeighborhoodEntity>>

    @Query("DELETE FROM neighborhoods WHERE name = :name")
    suspend fun deleteNeighborhood(name: String)

    // User Profile Deletion (Delete fake profiles)
    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: String)
}
