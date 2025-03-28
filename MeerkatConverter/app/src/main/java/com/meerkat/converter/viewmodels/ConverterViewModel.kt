package com.meerkat.converter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meerkat.converter.database.HistoryDao
import com.meerkat.converter.database.HistoryEntity
import kotlinx.coroutines.launch

class ConverterViewModel(private val historyDao: HistoryDao) : ViewModel() {

    fun saveToHistory(history: HistoryEntity) {
        viewModelScope.launch {
            historyDao.insert(history)
        }
    }
}