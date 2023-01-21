package com.puntogris.neonmaze.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.puntogris.neonmaze.R
import com.puntogris.neonmaze.databinding.FragmentMazeBinding
import com.puntogris.neonmaze.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MazeFragment : Fragment(R.layout.fragment_maze) {

    private val viewModel: GameViewModel by activityViewModels()
    private val binding by viewBinding(FragmentMazeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.timerDatabaseUpdated.run()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.getPlayersOnline().observe(viewLifecycleOwner) { playersOnline ->
            if (playersOnline != null) {
                binding.gameView.updatePlayersOnline(playersOnline)
            }
        }
        viewModel.getMazeSeed().observe(viewLifecycleOwner) { seed ->
            viewModel.updateMazeSeed(seed)
        }
        binding.gameView.playerCell.observe(viewLifecycleOwner) { position ->
            if (viewModel.playerFoundExit(position)) {
                viewModel.setNewSeed()
                binding.gameView.restartPlayerPosition()
            } else {
                viewModel.updatePlayerPos(position)
            }
        }
        viewModel.currentMaze.observe(viewLifecycleOwner) { newMaze ->
            binding.gameView.setMaze(newMaze)
            binding.gameView.restartPlayerPosition()
        }
    }

    override fun onStop() {
        viewModel.apply {
            deletePlayer()
            stopTimer()
        }
        super.onStop()
    }
}
