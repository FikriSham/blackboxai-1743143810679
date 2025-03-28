package com.meerkat.converter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meerkat.converter.database.HistoryDao
import com.meerkat.converter.database.HistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HistoryViewModel(private val historyDao: HistoryDao) : ViewModel() {

    val allHistory: Flow<List<HistoryEntity>> = historyDao.getAllHistory()

    fun clearHistory() {
        viewModelScope.launch {
            historyDao.clearHistory()
        }
    }
}