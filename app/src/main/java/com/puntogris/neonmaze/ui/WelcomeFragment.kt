package com.puntogris.neonmaze.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.neonmaze.R
import com.puntogris.neonmaze.databinding.FragmentWelcomeBinding
import com.puntogris.neonmaze.utils.viewBinding

class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    private val viewModel: GameViewModel by activityViewModels()
    private val binding by viewBinding(FragmentWelcomeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button.setOnClickListener {
            fetchMazeInformationFromDatabase()
        }
    }

    private fun fetchMazeInformationFromDatabase() {
        with(viewModel) {
            createPlayer()
            getMazeSeed().observe(viewLifecycleOwner) { seed ->
                updateMazeSeed(seed)
                navigateToMazeFragment()
            }
        }
        binding.loadingGroup.visibility = View.VISIBLE
        binding.button.visibility = View.GONE
    }

    private fun navigateToMazeFragment() {
        findNavController().navigate(R.id.action_welcomeFragment_to_mazeFragment)
    }
}
