package com.xlentdevs.spectrum.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.xlentdevs.spectrum.databinding.FragmentSplashScreenBinding
import com.xlentdevs.spectrum.ui.authentication.MainActivity
import com.xlentdevs.spectrum.ui.dashboard.DashBoardActivity
import com.xlentdevs.spectrum.utils.PreferenceStore
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SplashScreenFragment : Fragment() {

    private lateinit var binding: FragmentSplashScreenBinding
    private lateinit var prefs: PreferenceStore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSplashScreenBinding.inflate(inflater, container, false);
        binding.fragment = this
        binding.lifecycleOwner = this

        prefs = PreferenceStore(requireContext().applicationContext)

        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launch {
                prefs.authToken.collect { user ->
                    if (user!!.uid.equals("NA")) {
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        (activity as SplashScreenActivity).finish()
                    } else {
                        val intent = Intent(context, DashBoardActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        (activity as SplashScreenActivity).finish()
                    }
                }
            }
        }, 3000)

        return binding.root
    }
}