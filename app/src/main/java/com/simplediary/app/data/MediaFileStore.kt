package com.simplediary.app.data

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

class MediaFileStore(private val context: Context) {
    private val mediaDir: File by lazy {
        File(context.filesDir, "media").also { if (!it.exists()) it.mkdirs() }
    }

    fun copyFromUri(uri: Uri, type: MediaType): String {
        val extension = when (type) {
            MediaType.IMAGE -> guessExtension(uri, "jpg")
            MediaType.VIDEO -> guessExtension(uri, "mp4")
            MediaType.AUDIO -> guessExtension(uri, "m4a")
        }
        val dest = File(mediaDir, "${UUID.randomUUID()}.$extension")
        context.contentResolver.openInputStream(uri)?.use { input ->
            dest.outputStream().use { output -> input.copyTo(output) }
        } ?: error("Unable to read media uri")
        return dest.absolutePath
    }

    fun createCaptureFile(type: MediaType): File {
        val extension = when (type) {
            MediaType.IMAGE -> "jpg"
            MediaType.VIDEO -> "mp4"
            MediaType.AUDIO -> "m4a"
        }
        return File(mediaDir, "${UUID.randomUUID()}.$extension").also { it.createNewFile() }
    }

    fun deleteFile(path: String) {
        runCatching { File(path).takeIf { it.exists() }?.delete() }
    }

    private fun guessExtension(uri: Uri, fallback: String): String {
        val name = uri.lastPathSegment.orEmpty()
        val dot = name.lastIndexOf('.')
        return if (dot >= 0 && dot < name.length - 1) {
            name.substring(dot + 1).take(8)
        } else {
            fallback
        }
    }
}
