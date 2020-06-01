package com.utildev.navigation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NavigationAdapter(
    private val datas: List<String>,
    private val listener: AdapterItemListener
) : RecyclerView.Adapter<NavigationAdapter.NavigationViewHolder>() {
    class NavigationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textView: TextView? = null

        init {
            textView = view.findViewById(R.id.tv)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nav, parent, false)
        return NavigationViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: NavigationViewHolder, position: Int) {
        holder.textView?.text = datas[position]
        holder.textView?.setOnClickListener {
            listener.onItemClick(position)
        }
    }

    interface AdapterItemListener {
        fun onItemClick(position: Int)
    }
}