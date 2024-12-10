package com.bangkit.fishmate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.fishmate.data.Response.Article
import com.bangkit.fishmate.databinding.ItemNewsBinding
import com.bumptech.glide.Glide

class NewsAdapter(private val clickListener: (String) -> Unit) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private var newsList = mutableListOf<Article>()

    class NewsViewHolder(private val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article, clickListener: (String) -> Unit) {
            binding.tvTitle.text = article.title
            binding.tvDescription.text = article.description

            Glide.with(binding.ivImage.context)
                .load(article.urlToImage)
                .into(binding.ivImage)

            binding.root.setOnClickListener {
                clickListener(article.url!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(newsList[position], clickListener)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    fun setNews(news: List<Article>) {
        newsList.clear()
        newsList.addAll(news)
        notifyDataSetChanged()
    }
}
