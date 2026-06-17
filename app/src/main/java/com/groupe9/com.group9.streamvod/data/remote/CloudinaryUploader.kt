package com.groupe9.streamvod.data.remote

import android.content.Context
import android.net.Uri
import com.groupe9.streamvod.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudinaryUploader @Inject constructor() {

    private val client = OkHttpClient()

    suspend fun uploadVideo(context: Context, uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw Exception("Impossible de lire le fichier vidéo")
            val tempFile = File.createTempFile("upload_", ".mp4", context.cacheDir)
            tempFile.outputStream().use { output -> inputStream.copyTo(output) }

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("upload_preset", BuildConfig.CLOUDINARY_UPLOAD_PRESET)
                .addFormDataPart(
                    "file", tempFile.name,
                    tempFile.asRequestBody("video/mp4".toMediaTypeOrNull())
                )
                .build()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/${BuildConfig.CLOUDINARY_CLOUD_NAME}/video/upload")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            tempFile.delete()

            if (!response.isSuccessful) {
                throw Exception("Échec de l'upload : ${response.code}")
            }

            val json = JSONObject(response.body?.string() ?: "{}")
            val secureUrl = json.getString("secure_url")
            Result.success(secureUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}