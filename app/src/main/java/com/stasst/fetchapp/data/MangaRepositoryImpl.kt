package com.stasst.fetchapp.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.select.Elements

class MangaRepositoryImpl: MangaRepository {
    override suspend fun fetchData(url: String, currentPage: Int): MangaData {

        var imageUrls = mutableListOf<String>()
        val titles = mutableListOf<String>()
        val links = mutableListOf<String>()
        val tags = mutableListOf<List<String>>()
        val offset = (currentPage - 1) * 20

        val doc = withContext(Dispatchers.IO) { Jsoup.connect(url + offset).timeout(400 * 1000).get() }
        val mangaImages = doc.select(".manga_images")
        for (element in mangaImages) {
            // Находим все элементы img внутри текущего элемента
            val imgElements = element.select("img")

            // Получаем ссылки из атрибута src каждого найденного img элемента
            for (imgElement in imgElements) {
                val imageUrl = imgElement.attr("src")
                imageUrls.add(imageUrl)
            }
        }

        imageUrls = imageUrls.filter { it.startsWith("https://") }.toMutableList()

        Log.d("pageDoc", doc.toString())

        val contentRows = doc.select(".content_row")

        for (contentRow in contentRows) {
            val title = contentRow.attr("title") // Получаем значение атрибута title

            if (title.isNotEmpty()) {
                titles.add(title) // Добавляем значение в список titlesList
            }
        }

        val linkElements = doc.select("a[href^=/manga/]")
        val tagElements: List<Elements> = doc.select("div.genre").map { it.getElementsByTag("a") }

        for (linkElement in linkElements) {
            val link = linkElement.attr("href")
            if (link.isNotEmpty()) {
                links.add(link)
            }
        }


        tagElements.forEach { elements ->
            val tagList: List<String> = elements.eachText() // Получаем текст каждого тега
            tags.add(tagList) // Добавляем список тегов в MutableList
        }

        links.removeAll { it.length < 14 }
        for (i in links.size - 1 downTo 0 step 2) {
            links.removeAt(i)
        }

        Log.d("pageLinks", links.toString())

        return MangaData(imageUrls, titles, links, tags)
    }

    override suspend fun fetchTags(): MutableList<String> {
        val tags = mutableListOf<String>()
        val doc = withContext(Dispatchers.IO) { Jsoup.connect("https://manga-chan.me/catalog").timeout(400 * 1000).get() }

        val sideTags = doc.select("div.sidetags li.sidetag a[href^=/tags/]")

        for (tag in sideTags) {
            val tagName = tag.text()
            if(tagName != "-" && tagName != "+")
                tags.add(tagName)
        }
        Log.d("allTags", tags.toString())
        return tags
    }
}