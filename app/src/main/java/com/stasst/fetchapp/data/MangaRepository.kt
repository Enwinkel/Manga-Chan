package com.stasst.fetchapp.data

interface MangaRepository {
    suspend fun fetchData(url: String, currentPage: Int): MangaData
    suspend fun fetchTags(): MutableList<String>
}