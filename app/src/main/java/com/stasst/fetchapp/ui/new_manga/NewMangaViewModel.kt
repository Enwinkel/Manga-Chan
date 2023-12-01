package com.stasst.fetchapp.ui.new_manga

import android.util.Log
import androidx.compose.ui.text.capitalize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stasst.fetchapp.data.MangaData
import com.stasst.fetchapp.data.MangaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NewMangaViewModel @Inject constructor(
    private val mangaRepository: MangaRepository
) : ViewModel() {
    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _mangaData = MutableStateFlow<MangaData?>(null)
    val mangaData: StateFlow<MangaData?> = _mangaData

    private val _tags = MutableStateFlow(mutableListOf<String>())
    val tags: StateFlow<MutableList<String>> = _tags

    private val _title = MutableStateFlow("Новинки")
    val title: StateFlow<String> = _title

    private var url = "https://manga-chan.me/tags/-веб&sort=manga?offset="

    init {
        loadThePage(currentPage.value)
        loadTheTags()
    }

    private fun loadTheTags() {
        viewModelScope.launch {
            _tags.value = mangaRepository.fetchTags()
        }
    }

    private fun loadThePage(page: Int) {
        _loading.value = true
        viewModelScope.launch {
            val data = mangaRepository.fetchData(url, page)
            _mangaData.value = data
            _loading.value = false
        }
    }


    fun onEvent(event: NewMangaEvent) {
        when (event) {
            is NewMangaEvent.PageChangedEvent -> {
                _currentPage.value = event.pageIndex
                loadThePage(event.pageIndex)
            }
            is NewMangaEvent.MangaClickEvent -> {

            }
            is NewMangaEvent.TagClickEvent -> {
                if(event.tag == "Вся манга"){
                    url = "https://manga-chan.me/tags/-веб?offset="
                    _title.value = "Новинки"
                } else {
                    url = "https://manga-chan.me/tags/-веб+" + event.tag.replace(" ", "_") + "?offset="
                    Log.d("pageTagUrl", url)
                    _title.value = event.tag.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
                }
                _currentPage.value = 1

                loadThePage(1)
            }
            is NewMangaEvent.TagAddedEvent -> {
                url = url.substringBefore("&") + "+" + event.tag.replace(" ", "_") + "&sort=manga?offset="
                _title.value += " + " + event.tag
                Log.d("pageSeveral", url)
                _currentPage.value = 1
                loadThePage(1)
            }
        }
    }
}