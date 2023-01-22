package com.puntogris.neonmaze.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.puntogris.neonmaze.R
import com.puntogris.neonmaze.databinding.FragmentWelcomeBinding
import com.puntogris.neonmaze.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    private val viewModel: GameViewModel by activityViewModels()
    private val binding by viewBinding(FragmentWelcomeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonJoinMaze.setOnClickListener {
            fetchMazeInformationFromDatabase()
        }
    }

    private fun fetchMazeInformationFromDatabase() {
        lifecycleScope.launch {
            viewModel.createPlayer()
            findNavController().navigate(R.id.action_welcomeFragment_to_mazeFragment)
        }
        binding.groupLoading.visibility = View.VISIBLE
        binding.buttonJoinMaze.visibility = View.GONE
    }
}
