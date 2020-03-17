package com.utildev.pagingsample

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CheeseDao {
    @Query("SELECT * FROM Cheese ORDER BY name COLLATE NOCASE ASC")
    fun allCheeseByName(): DataSource.Factory<Int, Cheese>

    @Insert
    fun insert(cheeses: List<Cheese>)

    @Insert
    fun insert(cheese: Cheese)

    @Delete
    fun delete(cheese: Cheese)
}