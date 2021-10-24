package com.xlentdevs.spectrum.commons

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.xlentdevs.spectrum.R
import com.xlentdevs.spectrum.data.db.entity.ChatRoom
import com.xlentdevs.spectrum.data.db.entity.Message
import com.xlentdevs.spectrum.ui.dashboard.chatroom.ChatListAdapter
import com.xlentdevs.spectrum.ui.dashboard.chats.ChatViewModel
import com.xlentdevs.spectrum.ui.dashboard.chats.ChatsFragment
import com.xlentdevs.spectrum.ui.dashboard.chats.MessageListAdapter
import com.xlentdevs.spectrum.ui.dashboard.discover.DiscoverListAdapter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs


@BindingAdapter("bind_discover_list")
fun bindDiscoverList(listView: RecyclerView, items: List<ChatRoom>?) {
    items?.let {
        (listView.adapter as DiscoverListAdapter).submitList(items)
        listView.scrollToPosition(items.size - 1)
    }
}

@BindingAdapter("bind_chat_room_list")
fun bindChatRoomList(listView: RecyclerView, items: List<ChatRoom>?) {
    items?.let {
        (listView.adapter as ChatListAdapter).submitList(items)
        listView.scrollToPosition(items.size - 1)
    }
}

@BindingAdapter("bind_image_url")
fun bindImageWithGlide(imageView: ImageView, url: String?) {
    when (url) {
        null -> Unit
        "" -> imageView.setBackgroundResource(R.drawable.round_bg)
        else -> Glide.with(imageView.context).load(url).into(imageView)
    }
}

@BindingAdapter("bind_message", "bind_message_viewModel")
fun View.bindShouldMessageShowTimeText(message: Message, viewModel: ChatViewModel) {
    val halfHourInMilli = 1800000
    val index = viewModel.messagesList.value!!.indexOf(message)

    if (index == 0) {
        this.visibility = View.VISIBLE
    } else {
        val messageBefore = viewModel.messagesList.value!![index - 1]

        if (abs(messageBefore.epochTimeMs - message.epochTimeMs) > halfHourInMilli) {
            this.visibility = View.VISIBLE
        } else {
            this.visibility = View.GONE
        }
    }
}

@BindingAdapter("bind_message_view", "bind_message_textView", "bind_message", "bind_myUserID")
fun View.bindMessageSeen(view: View, textView: TextView, message: Message, myUserID: String) {
    if (message.senderId != myUserID && !message.seen) {
        view.visibility = View.VISIBLE
//        textView.alpha = 1f
    } else {
        view.visibility = View.INVISIBLE
//        textView.alpha = 1f
    }
}

@SuppressLint("SimpleDateFormat")
@BindingAdapter("bind_epochTimeMsToDate")
fun TextView.bindEpochTimeMsToDate(epochTimeMs: Long) {
    if (epochTimeMs > 0) {
        val currentTimeMs = Date().time
        val numOfDays = TimeUnit.MILLISECONDS.toDays(currentTimeMs - epochTimeMs)

        val replacePattern = when {
            numOfDays >= 1.toLong() -> "Yy"
            else -> "YyMd"
        }
        val pat = SimpleDateFormat().toLocalizedPattern().replace("\\W?[$replacePattern]+\\W?".toRegex(), " ")
        val formatter = SimpleDateFormat(pat, Locale.getDefault())
        this.text = formatter.format(Date(epochTimeMs))
    }
}

@BindingAdapter("bind_messages_list")
fun bindMessagesList(listView: RecyclerView, items: List<Message>?) {
    items?.let {
        (listView.adapter as MessageListAdapter).submitList(items)
        listView.scrollToPosition(items.size - 1)
    }
}
