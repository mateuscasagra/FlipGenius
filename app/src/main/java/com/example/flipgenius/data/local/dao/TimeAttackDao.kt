import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeAttackDao {

    // 'suspend' para rodar em uma corrotina
    @Insert
    suspend fun inserir(partida: PartidaTimeAttack)

    // 'Flow' para que a UI reaja automaticamente a mudanças no banco
    @Query("SELECT * FROM partidas_time_attack")
    fun getAllFlow(): Flow<List<PartidaTimeAttack>>

    @Query("DELETE FROM partidas_time_attack")
    suspend fun deleteAll()
}