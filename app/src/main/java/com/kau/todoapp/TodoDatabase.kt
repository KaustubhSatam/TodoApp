package com.kau.todoapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase(){
    abstract class taskDao(): TaskDao
    companion object{
        @Volatile
        private var INSTANCE : TodoDatabase? = null

        fun getDatabase(context: Context): TodoDatabase{
            return INSTANCE?: synchronized(this){
                val instance= Room.databaseBuilder(context.applicationContext,
                    TodoDatabase::class.java,"todo_database").build()
                INSTANCE = instance

                instance
            }
        }

    }
}