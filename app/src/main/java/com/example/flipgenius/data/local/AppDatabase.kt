package com.example.flipgenius.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flipgenius.data.local.dao.ConfigJogadorDao
import com.example.flipgenius.data.local.dao.UsuarioDao
import com.example.flipgenius.data.local.dao.PartidaDao
import com.example.flipgenius.data.local.dao.TimeAttackDao
import com.example.flipgenius.data.local.entities.ConfigJogador
import com.example.flipgenius.data.local.entities.Usuario
import com.example.flipgenius.data.local.entities.PartidaTimeAttack
import com.example.flipgenius.data.local.entities.Partida

/**
 * Banco de dados Room central do app.
 * Inclui entidades de usuário e configurações de jogador.
 */
@Database(
    entities = [Usuario::class, ConfigJogador::class, PartidaTimeAttack::class, Partida::class],
<<<<<<< HEAD
    version = 4,
=======
    version = 3,
>>>>>>> 025c903dee6341de1a55833344512f3a656bc1fa
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun configJogadorDao(): ConfigJogadorDao
    abstract fun timeAttackDao(): TimeAttackDao
    abstract fun partidaDao(): PartidaDao

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