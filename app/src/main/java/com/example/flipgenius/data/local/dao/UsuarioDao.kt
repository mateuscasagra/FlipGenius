import androidx.room.*
import androidx.room.OnConflictStrategy
import com.example.flipgenius.data.local.entities.Usuario

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usuario: Usuario)

    @Query("SELECT * FROM usuario WHERE nome = :nome AND senhaHash = :senhaHash LIMIT 1")
    suspend fun login(nome: String, senhaHash: String): Usuario?

    @Update
    suspend fun update(usuario: Usuario)

    @Query("DELETE FROM usuario WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM usuario WHERE nome = :nome LIMIT 1")
    suspend fun getUsuarioByNome(nome: String): Usuario?
}