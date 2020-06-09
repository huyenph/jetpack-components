package com.utildev.pagingnetworksample.adapter

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.utildev.pagingnetworksample.data.News
import com.utildev.pagingnetworksample.data.State

const val DATA_TYPE = 1
const val FOOTER_TYPE = 2

class NewsListAdapter(private val retry: () -> Unit) :
    PagedListAdapter<News, RecyclerView.ViewHolder>(NewsDiffCallback) {

    private var state = State.LOADING

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == DATA_TYPE)
            NewsViewHolder.create(parent)
        else ListFooterViewHolder.create(retry, parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == DATA_TYPE) {
            (holder as NewsViewHolder).bind(getItem(position))
        } else {
            (holder as ListFooterViewHolder).bind(state)
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (position < super.getItemCount()) DATA_TYPE else FOOTER_TYPE

    override fun getItemCount(): Int =
        super.getItemCount() + if (hasFooter()) 1 else 0


    private fun hasFooter(): Boolean =
        super.getItemCount() != 0 && (state == State.LOADING || state == State.ERROR)

    fun setState(state: State) {
        this.state = state
        notifyItemChanged(super.getItemCount())
    }

    companion object {
        val NewsDiffCallback = object : DiffUtil.ItemCallback<News>() {
            override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
                return oldItem == newItem
            }
        }
    }

}