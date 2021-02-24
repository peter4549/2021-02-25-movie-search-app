package com.grand.duke.elliot.madras.check.moviesearchapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.grand.duke.elliot.madras.check.moviesearchapp.R
import com.grand.duke.elliot.madras.check.moviesearchapp.databinding.ItemMovieBinding
import com.grand.duke.elliot.madras.check.moviesearchapp.network.MovieItem
import com.grand.duke.elliot.madras.check.moviesearchapp.util.setHtmlText

class MovieItemAdapter: ListAdapter<MovieItem, MovieItemAdapter.ViewHolder>(MovieDiffCallback()) {

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(item: MovieItem)
    }

    inner class ViewHolder(private val binding: ItemMovieBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MovieItem) {
            Glide.with(binding.imageView)
                .load(item.image)
                .error(R.drawable.ic_sharp_error_outline_24)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imageView)

            binding.textViewTitle.setHtmlText(item.title)
            binding.textViewPublicationDate.text = item.pubDate.toString() // todo check. format.
            binding.textViewUserRating.text = item.userRating.toString() // todo check format.
            binding.root.setOnClickListener {
                onItemClickListener?.onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class MovieDiffCallback: DiffUtil.ItemCallback<MovieItem>() {
    override fun areItemsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
        return oldItem.title == newItem.title && oldItem.pubDate == newItem.pubDate
    }

    override fun areContentsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
        return oldItem == newItem
    }
}