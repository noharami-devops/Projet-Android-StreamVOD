package com.groupe9.streamvod.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupe9.streamvod.data.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StreamPlayerViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    fun recordView(videoUrl: String, title: String) {
        viewModelScope.launch {
            historyRepository.addToHistory(
                itemId = "uservideo_$videoUrl",
                title = title,
                imageUrl = "",
                type = "user_video"
            )
        }
    }
}