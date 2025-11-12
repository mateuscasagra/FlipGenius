package com.example.flipgenius

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flipgenius.data.local.AppDatabase
import com.example.flipgenius.data.remote.FirebaseService
import com.example.flipgenius.data.repository.PartidaRepository
import com.example.flipgenius.data.repository.TemaRepository
import com.example.flipgenius.ui.viewmodels.JogoViewModel

class ViewModelFactory(
    private val database: AppDatabase,
    private val firebaseService: FirebaseService
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(JogoViewModel::class.java) -> {
                val temaRepository = TemaRepository(firebaseService)
                val partidaRepository = PartidaRepository(database.partidaDao())
                // Nota: usuarioId e temaId devem ser passados via parâmetros
                // Por enquanto, retornamos null - será ajustado na tela
                JogoViewModel(temaRepository, partidaRepository, null, null) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}


