package com.xlentdevs.spectrum.ui.dashboard.discover

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.xlentdevs.spectrum.R
import com.xlentdevs.spectrum.databinding.FragmentDiscoverBinding
import com.xlentdevs.spectrum.ui.authentication.signup.SignUpViewModel
import com.xlentdevs.spectrum.ui.authentication.signup.SignUpViewModelFactory

class DiscoverFragment : Fragment() {

    private val viewModel: DiscoverViewModel by viewModels{
        DiscoverViewModelFactory(
            requireNotNull(this.activity).application
        )
    }

    private lateinit var listAdapter: DiscoverListAdapter
    private lateinit var listAdapterObserver: RecyclerView.AdapterDataObserver
    private lateinit var binding: FragmentDiscoverBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDiscoverBinding.inflate(inflater, container, false)
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

        listAdapter = DiscoverListAdapter(viewModel, DiscoverItemListener{room->
            viewModel.onItemClicked(room)
        })

        listAdapter.registerAdapterDataObserver(listAdapterObserver)
        binding.recyclerView.adapter = listAdapter
    }
}