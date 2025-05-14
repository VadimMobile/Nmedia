package ru.netology.nmedia.repository

import androidx.lifecycle.*
import com.google.android.gms.common.api.Api
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import ru.netology.nmedia.api.*
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.File

class PostRepositoryImpl(private val dao: PostDao) : PostRepository {
    override val data = dao.getAll().map { it.map { it.toDto() } }

    override suspend fun getAll() {
        try {
            val response = PostsApi.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post) {
        try {
            val response = PostsApi.service.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post, file: File) {
        try {

            val media = upload(file)

            val response = PostsApi.service.save(post.copy(
                attachment = Attachment(
                    media.id, AttachmentType.IMAGE,
                )
            ))
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    private suspend fun upload(file: File): Media =
        PostsApi.service.upload(
            MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())
        )


    override suspend fun removeById(id: Long) {
        try {
            val response = PostsApi.service.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            dao.removeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(id: Long, isLike: Boolean) {
        try {
            val post = dao.getById(id)
            if (post != null) {
                dao.likeById(id)
                val response = if (isLike) {
                    PostsApi.service.dislikeById(id)
                } else {
                    PostsApi.service.likeById(id)
                }
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }
                dao.insert(
                    PostEntity.fromDto(
                        response.body() ?: throw ApiError(
                            response.code(),
                            response.message()
                        )
                    )
                )
            } else {
                throw UnknownError
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override fun getNewer(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000)
            val response = PostsApi.service.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insertSecondary(body.toEntity().map { it.copy(uploadPost = 1) })
            emit(body.size)
        }
    }.catch { e -> throw AppError.from(e) }
}
