package com.example.flipgenius.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flipgenius.data.repository.ConfigRepository
import com.example.flipgenius.ui.viewmodels.ConfigViewModel

/**
 * Factory simples para criar ViewModels com dependências.
 */
object ViewModelFactory {
    fun getFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(ConfigViewModel::class.java) -> {
                    val repo = ConfigRepository.create()
                    ConfigViewModel(repo) as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
            }
        }
    }
}