package com.example.flipgenius.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flipgenius.data.repository.ConfigRepository
import com.example.flipgenius.ui.viewmodels.ConfigViewModel
import com.example.flipgenius.ui.viewmodels.JogoViewModel
import com.example.flipgenius.ui.viewmodels.AdminViewModel

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
                modelClass.isAssignableFrom(AdminViewModel::class.java) -> {
                    AdminViewModel() as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
            }
        }
    }
    
    fun getJogoFactory(context: Context, tema: String): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(JogoViewModel::class.java) -> {
                    JogoViewModel(context, tema) as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
            }
        }
    }
}
