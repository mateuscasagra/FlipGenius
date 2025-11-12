package com.example.flipgenius.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseService {
    private val firestore = FirebaseFirestore.getInstance()
    
    suspend fun buscarTodosTemas(): List<Tema> {
        return try {
            val snapshot = firestore.collection("temas")
                .get()
                .await()
            
            snapshot.documents.map { doc ->
                Tema(
                    id = doc.id,
                    nome = doc.getString("nome") ?: "",
                    emojis = doc.getString("emojis") ?: ""
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun buscarTemaPorId(temaId: String): Tema? {
        return try {
            val doc = firestore.collection("temas")
                .document(temaId)
                .get()
                .await()
            
            if (doc.exists()) {
                Tema(
                    id = doc.id,
                    nome = doc.getString("nome") ?: "",
                    emojis = doc.getString("emojis") ?: ""
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}


