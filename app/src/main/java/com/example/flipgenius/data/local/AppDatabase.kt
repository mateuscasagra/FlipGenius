package com.example.flipgenius.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flipgenius.data.local.dao.ConfigJogadorDao
import com.example.flipgenius.data.local.dao.UsuarioDao
import com.example.flipgenius.data.local.entities.ConfigJogador
import com.example.flipgenius.data.local.entities.Usuario

/**
 * Banco de dados Room central do app.
 * Inclui entidades de usuário e configurações de jogador.
 */
@Database(
    entities = [Usuario::class, ConfigJogador::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun configJogadorDao(): ConfigJogadorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flipgenius.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}