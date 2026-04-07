package com.binariamente.conectatea.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.binariamente.conectatea.models.Aluno
import com.binariamente.conectatea.models.Pictograma
import com.binariamente.conectatea.models.TabelaItem
import com.binariamente.conectatea.models.TabelaPictograma

@Database(
    entities = [Aluno::class, Pictograma::class, TabelaPictograma::class, TabelaItem::class],
    version = 1,
    exportSchema = false
)
abstract class ConectaTEADatabase : RoomDatabase() {

    abstract fun conectaTEADao(): ConectaTEADao

    companion object {
        @Volatile
        private var INSTANCE: ConectaTEADatabase? = null

        fun getDatabase(context: Context): ConectaTEADatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ConectaTEADatabase::class.java,
                    "conectatea_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}