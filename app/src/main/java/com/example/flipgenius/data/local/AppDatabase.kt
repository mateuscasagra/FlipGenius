package com.example.flipgenius.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.flipgenius.data.local.dao.PartidaDao
import com.example.flipgenius.data.local.dao.UsuarioDao
import com.example.flipgenius.data.local.entities.Partida
import com.example.flipgenius.data.local.entities.Usuario

@Database(
    entities = [Usuario::class, Partida::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun partidaDao(): PartidaDao
}


