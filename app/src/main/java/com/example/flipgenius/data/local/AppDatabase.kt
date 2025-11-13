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

@Database(
    entities = [Usuario::class, ConfigJogador::class, PartidaTimeAttack::class, Partida::class],
    version = 4,
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
