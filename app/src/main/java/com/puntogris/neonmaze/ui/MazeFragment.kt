package com.puntogris.neonmaze.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.puntogris.neonmaze.R
import com.puntogris.neonmaze.databinding.FragmentMazeBinding
import kotlinx.android.synthetic.main.fragment_maze.*

class MazeFragment : Fragment() {
    private val viewModel: GameViewModel by activityViewModels()
    private val args: MazeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding:FragmentMazeBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_maze, container, false)

        binding.apply {
            gameView.playerId = args.playerCell.id
            gameView.setMaze(viewModel.maze.value!!)
            viewModel.startTimer.run()

            viewModel.getPlayersOnline().observe(viewLifecycleOwner, Observer {
                if(it != null){
                    gameView.playersOnline.value = it
                    gameView.invalidate()
                }
            })

            viewModel.getMazeSeed().observe(viewLifecycleOwner, Observer { seed ->
                viewModel.updateMazeSeed(seed)
            })

            gameView.playerCell.observe(viewLifecycleOwner, Observer {position ->
                if (viewModel.playerFoundExit(position)){
                    viewModel.setNewSeed()
                    gameView.restartPlayerPosition()
                } else viewModel.updatePlayerPos(position)
            })

            viewModel.maze.observe(viewLifecycleOwner, Observer {
                gameView.setMaze(viewModel.maze.value!!)
                gameView.restartPlayerPosition()
            })

        }

        // Inflate the layout for this fragment
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