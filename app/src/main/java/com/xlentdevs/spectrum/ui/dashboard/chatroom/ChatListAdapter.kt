package com.xlentdevs.spectrum.ui.dashboard.chatroom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xlentdevs.spectrum.data.db.entity.ChatRoom
import com.xlentdevs.spectrum.databinding.ChatRomListItemLayoutBinding

class ChatListAdapter internal constructor(
    private val viewModel: ChatRoomViewModel,
    val clickListener: ChatRoomItemListener
) : ListAdapter<ChatRoom, RecyclerView.ViewHolder>(ChatRoomDiffCallback()) {

    class ChatRoomViewHolder(private val binding: ChatRomListItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            clickListener: ChatRoomItemListener,
            viewModel: ChatRoomViewModel,
            item: ChatRoom
        ) {
            binding.viewModel = viewModel
            binding.clickListener = clickListener
            binding.chatRoom = item
            binding.executePendingBindings()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ChatRoomViewHolder).bind(clickListener, viewModel, getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = ChatRomListItemLayoutBinding.inflate(layoutInflater, parent, false)

        return ChatRoomViewHolder(binding)
    }
}

class ChatRoomDiffCallback : DiffUtil.ItemCallback<ChatRoom>() {
    override fun areItemsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
        return oldItem.uid == newItem.uid
    }
}

class ChatRoomItemListener(val clickListener: (room: ChatRoom) -> Unit) {
    fun onClick(room: ChatRoom) = clickListener(room)
}