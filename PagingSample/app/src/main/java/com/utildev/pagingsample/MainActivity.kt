package com.utildev.pagingsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        swipeToDelete()
        addButtonListener()
    }

    private fun addCheese() {
        val newCheese = edt_name.text.trim().toString()
        if (newCheese.isNotEmpty()) {
            viewModel.insert(newCheese)
            edt_name.setText("")
        }
    }

    private fun addButtonListener() {
        btn_insert.setOnClickListener {
            addCheese()
        }

        edt_name.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addCheese()
                return@setOnEditorActionListener true
            }
            false
        }

        edt_name.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                addCheese()
                return@setOnKeyListener true
            }
            false
        }
    }

    private fun swipeToDelete() {
        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int = makeMovementFlags(
                0,
                ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.END or ItemTouchHelper.START
            )

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                (viewHolder as CheeseAdapter.CheeseViewHolder).cheese?.let {
                    viewModel.remove(it)
                }
            }
        }).attachToRecyclerView(rcv_content)
    }
}
