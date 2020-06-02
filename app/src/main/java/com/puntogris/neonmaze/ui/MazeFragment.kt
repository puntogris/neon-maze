package com.puntogris.neonmaze.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.puntogris.neonmaze.R
import com.puntogris.neonmaze.databinding.FragmentMazeBinding

class MazeFragment : Fragment() {
    private val viewModel: GameViewModel by activityViewModels()
    private val args: MazeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding:FragmentMazeBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_maze, container, false)

        binding.gameView.playerId = args.playerCell.id
        binding.gameView.setMaze(viewModel.maze.value!!)
        viewModel.startTimer.run()
        viewModel.getPlayersOnline().observe(viewLifecycleOwner, Observer {
            if(it != null){
                binding.gameView.playersOnlineList.value = it
                binding.gameView.invalidate()
            }
        })

        viewModel.getMazeSeed().observe(viewLifecycleOwner, Observer {
            binding.gameView.restartPlayerPosition()
        })

        binding.gameView.playerCell.observe(viewLifecycleOwner, Observer {
            if (viewModel.checkIfPlayerFoundExit(it)) binding.gameView.restartPlayerPosition()
            viewModel.updatePlayerPos(it)
        })


        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onPause() {
        viewModel.deletePlayer()
        viewModel.stopTimer()
        super.onPause()
    }


}