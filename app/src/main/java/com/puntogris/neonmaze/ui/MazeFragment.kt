package com.puntogris.neonmaze.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
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
        viewModel.updateDatabaseTimer.run()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.onlinePlayers.observe(viewLifecycleOwner) { players ->
            binding.gameView.updatePlayersOnline(players)
        }
        viewModel.currentMaze.observe(viewLifecycleOwner) { newMaze ->
            binding.gameView.setMaze(newMaze)
            binding.gameView.restartPlayerPosition()
        }
        binding.gameView.setPlayerMoveListener { position ->
            if (viewModel.playerFoundExit(position)) {
                viewModel.setNewSeed()
                binding.gameView.restartPlayerPosition()
            } else {
                viewModel.updatePlayerPos(position)
            }
        }
    }

    override fun onStop() {
        viewModel.deletePlayer()
        viewModel.stopTimer()
        super.onStop()
    }
}
