package com.groupe9.streamvod.data.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.groupe9.streamvod.data.remote.CloudinaryUploader
import com.groupe9.streamvod.domain.model.UserVideo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserVideoRepository @Inject constructor(
    private val cloudinaryUploader: CloudinaryUploader
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getAllUserVideos(): Flow<List<UserVideo>> = callbackFlow {
        val listener = firestore.collection("user_videos")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Important : ne plus avaler l'erreur silencieusement
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val videos = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(UserVideo::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(videos)
            }
        awaitClose { listener.remove() }
    }

    suspend fun uploadVideo(
        context: Context,
        uri: Uri,
        title: String,
        description: String
    ): Result<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("Non connecté")

            val uploadResult = cloudinaryUploader.uploadVideo(context, uri)
            val videoUrl = uploadResult.getOrThrow()

            val userVideo = UserVideo(
                title = title,
                description = description,
                videoUrl = videoUrl,
                uploaderEmail = user.email ?: "Anonyme",
                uploaderName = user.displayName?.takeIf { it.isNotBlank() }
                    ?: user.email?.substringBefore("@")
                    ?: "Anonyme",
                uploaderId = user.uid,
                timestamp = System.currentTimeMillis()
            )
            firestore.collection("user_videos").add(userVideo).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleLike(videoId: String, currentLikes: Int): Result<Unit> {
        return try {
            firestore.collection("user_videos").document(videoId)
                .update("likes", currentLikes + 1).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun deleteVideo(videoId: String, uploaderId: String): Result<Unit> {
        return try {
            val currentUser = auth.currentUser ?: throw Exception("Non connecté")
            if (currentUser.uid != uploaderId) {
                throw Exception("Vous ne pouvez supprimer que vos propres vidéos")
            }
            firestore.collection("user_videos").document(videoId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUserVideosByUploader(uploaderId: String): Flow<List<UserVideo>> = callbackFlow {
        val listener = firestore.collection("user_videos")
            .whereEqualTo("uploaderId", uploaderId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val videos = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(UserVideo::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(videos)
            }
        awaitClose { listener.remove() }
    }
}