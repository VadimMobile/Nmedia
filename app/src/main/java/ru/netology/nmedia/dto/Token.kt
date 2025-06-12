package ru.netology.nmedia.dto

import com.google.gson.annotations.SerializedName

data class Token(
    @SerializedName("id")
    val id: Long,
    @SerializedName("token")
    val token: String,
)