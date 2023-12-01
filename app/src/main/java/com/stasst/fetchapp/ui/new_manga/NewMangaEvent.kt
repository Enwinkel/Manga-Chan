package com.stasst.fetchapp.ui.new_manga

sealed class NewMangaEvent {
    data class MangaClickEvent(val link: String): NewMangaEvent()
    data class TagClickEvent(val tag: String): NewMangaEvent()
    data class PageChangedEvent(val pageIndex: Int): NewMangaEvent()
    data class TagAddedEvent(val tag: String): NewMangaEvent()

}