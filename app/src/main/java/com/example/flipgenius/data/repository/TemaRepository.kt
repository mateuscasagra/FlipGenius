package com.example.flipgenius.data.repository

import com.example.flipgenius.data.remote.FirebaseService
import com.example.flipgenius.data.remote.Tema

class TemaRepository(
    private val firebaseService: FirebaseService
) {
    suspend fun buscarTodosTemas(): List<Tema> {
        return firebaseService.buscarTodosTemas()
    }
    
    suspend fun buscarTemaPorId(temaId: String): Tema? {
        return firebaseService.buscarTemaPorId(temaId)
    }
}


