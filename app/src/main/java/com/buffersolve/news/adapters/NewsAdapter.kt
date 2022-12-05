package com.buffersolve.news.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.buffersolve.news.R
import com.buffersolve.news.models.Article
import com.bumptech.glide.Glide
import com.google.android.material.elevation.SurfaceColors

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder (itemView: View): RecyclerView.ViewHolder(itemView)

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
        return  ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_article_preview,
                parent,
                false
            )
        )
    }

    // BindViewHolder
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {

        val  article = differ.currentList[position]

        holder.itemView.apply {

            Glide.with(this).load(article.urlToImage).into(findViewById(R.id.ivArticleImage))
//            findViewById<TextView>(R.id.tvSource).text = article.source.name
            findViewById<TextView>(R.id.tvTitle).text = article.title
//            findViewById<TextView>(R.id.tvDescription).text = article.description
//            findViewById<TextView>(R.id.tvPublishedAt).text = article.publishedAt

            // Card Dynamic Color
            val cardView = findViewById<CardView>(R.id.cardView)
            cardView.setCardBackgroundColor(SurfaceColors.SURFACE_2.getColor(context))


//            setOnClickListener {
//                onItemClickListener?.let {
//                    it(article)
//                }
//            }

            findViewById<ImageView>(R.id.ivArticleImage).setOnClickListener {
                onItemClickListener?.let {
                    it(article)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    // ClickListener
    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
}