package com.example.flipgenius.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flipgenius.data.repository.ConfigRepository
import com.example.flipgenius.ui.viewmodels.ConfigViewModel
import com.example.flipgenius.ui.viewmodels.JogoViewModel
import com.example.flipgenius.ui.viewmodels.RankingViewModel
import com.example.flipgenius.ui.viewmodels.TimeAttackRankingViewModel
import com.example.flipgenius.ui.viewmodels.TimeAttackViewModel
import com.example.flipgenius.ui.viewmodels.AdminViewModel
import com.example.flipgenius.data.repository.PartidaRepository
import com.example.flipgenius.data.repository.TimeAttackRepository
import com.example.flipgenius.data.repository.TemaRepository
import com.example.flipgenius.ui.utils.SessionManager

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

    fun getRankingFactory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(RankingViewModel::class.java) -> {
                    val repo = PartidaRepository.create()
                    RankingViewModel(repo) as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
            }
        }
    }

    fun getTimeAttackRankingFactory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(TimeAttackRankingViewModel::class.java) -> {
<<<<<<< HEAD
                    val db = AppDatabase.getInstance(context)
                    val repo = TimeAttackRepository(db.timeAttackDao())
                    val session = SessionManager(context.applicationContext)
                    TimeAttackRankingViewModel(repo, session) as T
=======
                    val repo = TimeAttackRepository.create()
                    TimeAttackRankingViewModel(repo) as T
>>>>>>> 20f133e (FIREBASE)
                }
                else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
            }
        }
    }

    fun getTimeAttackFactory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(TimeAttackViewModel::class.java) -> {
                    val timeRepo = TimeAttackRepository.create()
                    val temaRepo = TemaRepository()
                    val session = SessionManager(context.applicationContext)
                    TimeAttackViewModel(timeRepo, temaRepo, session) as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
            }
        }
    }
}
