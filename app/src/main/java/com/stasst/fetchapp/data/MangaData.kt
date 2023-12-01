package com.stasst.fetchapp.data

data class MangaData (
    val imageUrls: List<String>,
    val titles: List<String>?,
    val links: List<String>,
    val tags: List<List<String>>
)