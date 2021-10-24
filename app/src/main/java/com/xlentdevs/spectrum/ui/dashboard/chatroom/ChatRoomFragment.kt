package com.xlentdevs.spectrum.ui.dashboard.chatroom

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.xlentdevs.spectrum.R
import com.xlentdevs.spectrum.databinding.FragmentChatRoomBinding
import com.xlentdevs.spectrum.databinding.FragmentDiscoverBinding
import com.xlentdevs.spectrum.ui.dashboard.discover.DiscoverItemListener
import com.xlentdevs.spectrum.ui.dashboard.discover.DiscoverListAdapter
import com.xlentdevs.spectrum.ui.dashboard.discover.DiscoverViewModel
import com.xlentdevs.spectrum.ui.dashboard.discover.DiscoverViewModelFactory

class ChatRoomFragment : Fragment() {

    private val viewModel: ChatRoomViewModel by viewModels{
        ChatRoomViewModelFactory(
            requireNotNull(this.activity).application
        )
    }

    private lateinit var listAdapter: ChatListAdapter
    private lateinit var listAdapterObserver: RecyclerView.AdapterDataObserver
    private lateinit var binding: FragmentChatRoomBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupListAdapter()

        return binding.root
    }

    private fun setupListAdapter() {
        listAdapterObserver = (object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                binding.recyclerView.scrollToPosition(positionStart)
            }
        })

        listAdapter = ChatListAdapter(viewModel, ChatRoomItemListener{ room->
            findNavController().navigate(ChatRoomFragmentDirections.actionChatRoomFragmentToChatsFragment(room.uid, room.roomName, room.roomPhoto, room.description, room.tag1))
        })

        listAdapter.registerAdapterDataObserver(listAdapterObserver)
        binding.recyclerView.adapter = listAdapter
    }
}