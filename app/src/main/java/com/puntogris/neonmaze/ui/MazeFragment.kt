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
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.onlinePlayers.observe(viewLifecycleOwner, binding.gameView::updatePlayersOnline)
        viewModel.currentMaze.observe(viewLifecycleOwner, binding.gameView::setMaze)
        binding.gameView.setPlayerMoveListener(viewModel::onPlayerChangedPosition)
    }

    override fun onStop() {
        viewModel.deletePlayer()
        super.onStop()
    }
}
