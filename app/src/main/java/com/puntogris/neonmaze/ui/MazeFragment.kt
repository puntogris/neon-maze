package com.puntogris.neonmaze.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.puntogris.neonmaze.databinding.FragmentMazeBinding
import com.puntogris.neonmaze.utils.viewBinding

class MazeFragment : Fragment() {
    private val viewModel: GameViewModel by activityViewModels()

    private val binding by viewBinding(FragmentMazeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewModel.timerDatabaseUpdated.run()

            viewModel.getPlayersOnline().observe(viewLifecycleOwner) { playersOnline ->
                playersOnline?.let {
                    gameView.updatePlayersOnline(playersOnline)
                }
            }

            viewModel.getMazeSeed().observe(viewLifecycleOwner) { seed ->
                viewModel.updateMazeSeed(seed)
            }

            gameView.playerCell.observe(viewLifecycleOwner, Observer { position ->
                if (viewModel.playerFoundExit(position)){
                    viewModel.setNewSeed()
                    gameView.restartPlayerPosition()
                } else viewModel.updatePlayerPos(position)
            })

            viewModel.currentMaze.observe(viewLifecycleOwner) { newMaze ->
                gameView.setMaze(newMaze)
                gameView.restartPlayerPosition()
            }
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
