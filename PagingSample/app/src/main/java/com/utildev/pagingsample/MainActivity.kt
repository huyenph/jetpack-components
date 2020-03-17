package com.utildev.pagingsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<CheeseViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cheeseAdapter = CheeseAdapter()
        rcv_content.run {
            adapter = cheeseAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        viewModel.allCheeses.observe(this, Observer(cheeseAdapter::submitList))
    }
}
