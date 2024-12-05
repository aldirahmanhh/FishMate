package com.bangkit.fishmate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.fishmate.data.Response.Article
import com.bangkit.fishmate.databinding.ItemNewsBinding
import com.bumptech.glide.Glide


class NewsAdapter : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private var newsList = mutableListOf<Article>()

    class NewsViewHolder(private val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article) {
            binding.tvTitle.text = article.title
            binding.tvDescription.text = article.description

            Glide.with(binding.ivImage.context)
                .load(article.urlToImage)
                .into(binding.ivImage)

            binding.root.setOnClickListener {
                TODO("Implement news item click action")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(newsList[position])
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
