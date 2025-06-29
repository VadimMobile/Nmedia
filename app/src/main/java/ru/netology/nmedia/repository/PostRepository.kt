package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import java.io.File

interface PostRepository {
    val data: Flow<PagingData<Post>>

    suspend fun save(post: Post)
    suspend fun save(post: Post, file: File)
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long, isLike: Boolean)
    suspend fun upload(upload: MediaUpload): Media
}
