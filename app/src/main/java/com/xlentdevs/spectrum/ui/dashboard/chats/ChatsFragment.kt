package com.xlentdevs.spectrum.ui.dashboard.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.xlentdevs.spectrum.R
import com.xlentdevs.spectrum.databinding.FragmentChatsBinding
import com.xlentdevs.spectrum.ui.dashboard.DashBoardActivity
import kotlinx.android.synthetic.main.activity_dashboard.*

class ChatsFragment : Fragment() {

    private val args : ChatsFragmentArgs by navArgs()

    private val viewModel: ChatViewModel by viewModels{
        ChatViewModelFactory(
            requireNotNull(this.activity).application,
            args.roomUid,
            args.roomName,
            args.roomPhoto,
            args.roomDescription,
            args.roomTag
        )
    }

    private lateinit var binding: FragmentChatsBinding
    private lateinit var listAdapter: MessageListAdapter
    private lateinit var listAdapterObserver: RecyclerView.AdapterDataObserver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater, container, false)
        binding.fragment = this
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        (activity as DashBoardActivity).bottom_nav.visibility = View.GONE

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupListAdapter()
    }

    private fun setupListAdapter() {
        listAdapterObserver = (object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                binding.messagesRecyclerView.scrollToPosition(positionStart)
            }
        })

        listAdapter = MessageListAdapter(viewModel, args.roomUid)
        listAdapter.registerAdapterDataObserver(listAdapterObserver)
        binding.messagesRecyclerView.adapter = listAdapter
    }

    fun endChat(){
        findNavController().popBackStack(R.id.chatRoomFragment, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listAdapter.unregisterAdapterDataObserver(listAdapterObserver)
    }
}