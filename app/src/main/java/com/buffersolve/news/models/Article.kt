package com.buffersolve.news.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity (
    tableName = "articles"
        )
@Parcelize
data class Article(
    @PrimaryKey (autoGenerate = true)
    var id: Int? = null,

    val author: String,
    val content: String,
    val description: String,
    val publishedAt: String,
    val title: String,
    val url: String,
    val urlToImage: String
) : Parcelable