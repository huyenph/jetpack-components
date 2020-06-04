package com.utildev.paging.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.utildev.paging.Injection
import com.utildev.paging.R
import kotlinx.android.synthetic.main.activity_search_repository.*

class SearchRepositoryActivity : AppCompatActivity() {
    private lateinit var viewModel: SearchRepositoryViewModel
    private val adapter = RepoAdapter()

    companion object {
        private const val LAST_SEARCH_QUERY: String = "last_search_query"
        private const val DEFAULT_QUERY = "Android"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_repository)

        // get the view model
        viewModel = ViewModelProvider(this, Injection.provideViewModelFactory(this)).get(
            SearchRepositoryViewModel::class.java
        )

        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        list.addItemDecoration(decoration)
//        setupScrollListener()

        initAdapter()
        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        viewModel.searchRepo(query)
        initSearch(query)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_QUERY, viewModel.lastQueryValue())
    }

    private fun initAdapter() {
        list.adapter = adapter
        viewModel.repos.observe(this, Observer {
            Log.d("Activity", "list: ${it?.size}")
            Log.d("aaa", "items: $it")
            showEmptyList(it?.size == 0)
            adapter.submitList(it)
        })
        viewModel.networkErrors.observe(this, Observer {
            Toast.makeText(this, "\uD83D\uDE28 Wooops $it", Toast.LENGTH_LONG).show()
        })
    }

    private fun initSearch(query: String) {
        search_repo.setText(query)

        search_repo.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput()
                true
            } else {
                false
            }
        }
        search_repo.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput()
                true
            } else {
                false
            }
        }
    }

    private fun updateRepoListFromInput() {
        search_repo.text.trim().let {
            if (it.isNotEmpty()) {
                list.scrollToPosition(0)
                viewModel.searchRepo(it.toString())
                adapter.submitList(null)
            }
        }
    }

    private fun showEmptyList(show: Boolean) {
        if (show) {
            emptyList.visibility = View.VISIBLE
            list.visibility = View.GONE
        } else {
            emptyList.visibility = View.GONE
            list.visibility = View.VISIBLE
        }
    }

//    private fun setupScrollListener() {
//        val layoutManager = list.layoutManager as androidx.recyclerview.widget.LinearLayoutManager
//        list.addOnScrollListener(object :
//            androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
//            override fun onScrolled(
//                recyclerView: androidx.recyclerview.widget.RecyclerView,
//                dx: Int,
//                dy: Int
//            ) {
//                super.onScrolled(recyclerView, dx, dy)
//                val totalItemCount = layoutManager.itemCount
//                val visibleItemCount = layoutManager.childCount
//                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
//
//                viewModel.listScrolled(visibleItemCount, lastVisibleItem, totalItemCount)
//            }
//        })
//    }
}
