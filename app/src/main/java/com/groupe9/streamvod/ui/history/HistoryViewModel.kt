package com.groupe9.streamvod.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupe9.streamvod.data.local.HistoryEntity
import com.groupe9.streamvod.data.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val isLoading: Boolean = false,
    val items: List<HistoryEntity> = emptyList()
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState

    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            historyRepository.getAllHistory().collect { items ->
                _uiState.value = _uiState.value.copy(items = items, isLoading = false)
            }
        }
    }

    fun removeItem(itemId: String) {
        viewModelScope.launch {
            historyRepository.removeFromHistory(itemId)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            historyRepository.clearHistory()
        }
    }
}