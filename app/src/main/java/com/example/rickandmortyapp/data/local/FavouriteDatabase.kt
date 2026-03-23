package com.example.rickandmortyapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.rickandmortyapp.utils.FAVOURITES_DB_NAME

@Database(entities = [FavouriteEntity::class], version = 1)
abstract class FavouriteDatabase : RoomDatabase() {

    abstract fun favouriteDao(): FavouriteDao

    companion object {
        @Volatile
        private var instance: FavouriteDatabase? = null

        fun getInstance(context: Context): FavouriteDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    FavouriteDatabase::class.java,
                    FAVOURITES_DB_NAME
                ).build().also { instance = it }
            }
    }
}