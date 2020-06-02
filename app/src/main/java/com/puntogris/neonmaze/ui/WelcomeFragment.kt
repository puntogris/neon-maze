package com.puntogris.neonmaze.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.puntogris.neonmaze.R
import com.puntogris.neonmaze.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {
    private val viewModel: GameViewModel by activityViewModels()

    private lateinit var binding:FragmentWelcomeBinding
        override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_welcome, container, false)

        binding.welcomeFragment = this
        viewModel.maze.observe(viewLifecycleOwner, Observer {})

        return binding.root
    }

    private fun navigateToMazeFragment(){
        val action =
            WelcomeFragmentDirections.actionWelcomeFragmentToMazeFragment(
                viewModel.playerCell.value!!,
                viewModel.seed.value!!)
        findNavController().navigate(action)
    }

    fun fetchMazeInformationFromDatabase(){
        viewModel.createPlayer()
        viewModel.getMazeSeed().observe(viewLifecycleOwner, Observer { seed ->
            viewModel.updateMazeSeed(seed)
            navigateToMazeFragment()
        })
        binding.apply {
            loadingMaze.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
            button.visibility = View.GONE
        }
    }

}