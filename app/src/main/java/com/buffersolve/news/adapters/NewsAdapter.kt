package com.buffersolve.news.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.buffersolve.news.R
import com.buffersolve.news.databinding.ItemArticlePreviewBinding
import com.buffersolve.news.models.Article
import com.bumptech.glide.Glide
import com.google.android.material.elevation.SurfaceColors

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder (val binding: ItemArticlePreviewBinding): RecyclerView.ViewHolder(binding.root)

    // DiffUtil
    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return  oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    // CreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticlePreviewBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)

    }

    // BindViewHolder
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        with(holder){
            val article = differ.currentList[position]

            // Img & Title
            Glide.with(holder.itemView.context).load(article.urlToImage).into(binding.ivArticleImage)
            binding.tvTitle.text = article.title

            // Card Dynamic Color
            val cardView = binding.cardView
            cardView.setCardBackgroundColor(SurfaceColors.SURFACE_2.getColor(holder.itemView.context))

            // Listeners
            binding.ivArticleImage.setOnClickListener { onItemClickListener?.let { it(article) } }
            binding.tvTitle.setOnClickListener { onItemClickListener?.let { it(article) } }

            // Animation
            binding.cardView.startAnimation(AnimationUtils
                .loadAnimation(holder.itemView.context, R.anim.recycle))
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    // Click Listener
    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
}