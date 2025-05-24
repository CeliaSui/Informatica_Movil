package es.uniovi.appasturiasbodegas.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import es.uniovi.appasturiasbodegas.data.Converters

@Database(entities = [Bodega::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class) // Añade los convertidores necesarios para manejar tipos complejos
abstract class BodegaDatabase : RoomDatabase() {
    abstract fun bodegaDAO(): BodegaDAO

    companion object {
        @Volatile
        private var INSTANCE: BodegaDatabase? = null

        fun getDatabase(context: Context): BodegaDatabase? {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BodegaDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // Destruye la base de datos si hay un cambio en el esquema
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}