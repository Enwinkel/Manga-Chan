package com.stasst.fetchapp.util

sealed class UiEvent {
    object PopBackStack: UiEvent()
    data class Navigate(val route: String): UiEvent()
}