package com.xlentdevs.spectrum.ui.dashboard.discover

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xlentdevs.spectrum.data.db.entity.ChatRoom
import com.xlentdevs.spectrum.databinding.DiscoverListItemLayoutBinding

class DiscoverListAdapter internal constructor(
    private val viewModel: DiscoverViewModel,
    val clickListener: DiscoverItemListener
) : ListAdapter<ChatRoom, RecyclerView.ViewHolder>(DiscoverDiffCallback()) {

    class DiscoverViewHolder(private val binding: DiscoverListItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            clickListener: DiscoverItemListener,
            viewModel: DiscoverViewModel,
            item: ChatRoom
        ) {
            binding.viewModel = viewModel
            binding.clickListener = clickListener
            binding.chatRoom = item
            binding.executePendingBindings()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DiscoverViewHolder).bind(clickListener, viewModel, getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = DiscoverListItemLayoutBinding.inflate(layoutInflater, parent, false)

        return DiscoverViewHolder(binding)
    }
}

class DiscoverDiffCallback : DiffUtil.ItemCallback<ChatRoom>() {
    override fun areItemsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
        return oldItem.uid == newItem.uid
    }
}

class DiscoverItemListener(val clickListener: (room: ChatRoom) -> Unit) {
    fun onClick(room: ChatRoom) = clickListener(room)
}