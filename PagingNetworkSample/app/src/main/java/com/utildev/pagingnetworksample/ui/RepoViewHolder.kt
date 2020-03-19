package com.utildev.pagingnetworksample.ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.utildev.pagingnetworksample.R
import com.utildev.pagingnetworksample.model.Repo
import kotlinx.android.synthetic.main.item_repo.view.*

class RepoViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    private var repo: Repo? = null

    companion object {
        fun create(parent: ViewGroup): RepoViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_repo, parent, false)
            return RepoViewHolder(view)
        }
    }

    init {
        view.setOnClickListener {
            repo?.url?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                view.context.startActivity(intent)
            }
        }
    }

    fun bind(repo: Repo?) {
        if (repo == null) {
            val resources = itemView.resources
            view.repo_name.text = resources.getString(R.string.loading)
            view.repo_description.visibility = View.GONE
            view.repo_language.visibility = View.GONE
            view.repo_stars.text = resources.getString(R.string.unknown)
            view.repo_forks.text = resources.getString(R.string.unknown)
        } else {
            showRepoData(repo)
        }
    }

    private fun showRepoData(repo: Repo) {
        this.repo = repo
        view.repo_name.text = repo.fullName

        // if the description is missing, hide the TextView
        var descriptionVisibility = View.GONE
        if (repo.description.isNotEmpty()) {
            view.repo_description.text = repo.description
            descriptionVisibility = View.VISIBLE
        }
        view.repo_description.visibility = descriptionVisibility

        view.repo_stars.text = repo.stars.toString()
        view.repo_forks.text = repo.forks.toString()

        // if the language is missing, hide the label and the value
        var languageVisibility = View.GONE
        if (!repo.language.isNullOrEmpty()) {
            val resources = this.itemView.context.resources
            view.repo_language.text = resources.getString(R.string.language, repo.language)
            languageVisibility = View.VISIBLE
        }
        view.repo_language.visibility = languageVisibility
    }

}