package com.utildev.pagingsample

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.paging.Config
import androidx.paging.toLiveData

class CheeseViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = CheeseDB.get(context = application.applicationContext).cheeseDao()

    val allCheeses = dao.allCheeseByName().toLiveData(
        Config(
            pageSize = 50, enablePlaceholders = true, maxSize = 200
        )
    )

    fun insert(name: CharSequence) = ioThread {
        dao.insert(Cheese(id = 0, name = name.toString()))
    }

    fun remove(cheese: Cheese) = ioThread {
        dao.delete(cheese)
    }
}