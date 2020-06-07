package com.puntogris.neonmaze.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.puntogris.neonmaze.R
import com.puntogris.neonmaze.databinding.FragmentMazeBinding

class MazeFragment : Fragment() {
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding:FragmentMazeBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_maze, container, false)

        binding.apply {
            viewModel.timerDatabaseUpdated.run()

            viewModel.getPlayersOnline().observe(viewLifecycleOwner, Observer { playersOnline ->
                playersOnline?.let {
                    gameView.updatePlayersOnline(playersOnline)
                }
            })

            viewModel.getMazeSeed().observe(viewLifecycleOwner, Observer { seed ->
                viewModel.updateMazeSeed(seed)
            })

            gameView.playerCell.observe(viewLifecycleOwner, Observer { position ->
                if (viewModel.playerFoundExit(position)){
                    viewModel.setNewSeed()
                    gameView.restartPlayerPosition()
                } else viewModel.updatePlayerPos(position)
            })

            viewModel.currentMaze.observe(viewLifecycleOwner, Observer { newMaze ->
                gameView.setMaze(newMaze)
                gameView.restartPlayerPosition()
            })
        }

        return binding.root
    }

    override fun onStop() {
        viewModel.apply {
            deletePlayer()
            stopTimer()
        }
        super.onStop()
    }
}