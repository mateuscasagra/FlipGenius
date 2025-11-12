package com.example.flipgenius.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flipgenius.data.repository.ConfigRepository
import com.example.flipgenius.ui.viewmodels.ConfigViewModel

/**
 * Factory simples para criar ViewModels com dependências.
 */
object ViewModelFactory {
    fun getFactory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(ConfigViewModel::class.java) -> {
                    val repo = ConfigRepository.create(context.applicationContext)
                    ConfigViewModel(repo) as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
            }
        }
    }
}