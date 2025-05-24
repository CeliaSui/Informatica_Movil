package es.uniovi.appasturiasbodegas.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow
import androidx.room.Query

@Dao
interface BodegaDAO {

    @Query("SELECT * FROM bodega_table WHERE id = :id")
    fun getBodegaById(id: Int): Flow<Bodega>

    @Query("SELECT * FROM bodega_table")
    fun getAllBodegas(): Flow<List<Bodega>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBodegas(bodegas: List<Bodega>)

    @Query("UPDATE bodega_table SET isFavorite = :favorite WHERE id = :id")
    suspend fun updateFavorite(id: Int, favorite: Boolean)

    @Query("SELECT * FROM bodega_table WHERE isFavorite = 1")
    fun getFavoriteBodegas(): Flow<List<Bodega>>

    @Query("DELETE FROM bodega_table")
    suspend fun clearAll()


}