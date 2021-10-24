package com.xlentdevs.spectrum.ui.dashboard.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.xlentdevs.spectrum.R
import com.xlentdevs.spectrum.databinding.FragmentDiscoverBinding
import com.xlentdevs.spectrum.databinding.FragmentHomeBinding
import com.xlentdevs.spectrum.ui.dashboard.discover.DiscoverListAdapter
import com.xlentdevs.spectrum.ui.dashboard.discover.DiscoverViewModel
import com.xlentdevs.spectrum.ui.dashboard.discover.DiscoverViewModelFactory

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels{
        HomeViewModelFactory(
            requireNotNull(this.activity).application
        )
    }

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }
}