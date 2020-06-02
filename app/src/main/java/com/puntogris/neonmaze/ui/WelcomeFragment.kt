package com.puntogris.neonmaze.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
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

        binding.button.setOnClickListener {
            loadMaze()

        }
        viewModel.maze.observe(viewLifecycleOwner, Observer {
        })

        return binding.root
    }

    fun navigateToMaze(){
        val action = WelcomeFragmentDirections.
        actionWelcomeFragmentToMazeFragment(
            viewModel.playerCell.value!!,
            viewModel.seed.value!!)
        findNavController().navigate(action)
    }

    fun loadMaze(){
        viewModel.createPlayer()
        viewModel.getMazeSeed().observe(viewLifecycleOwner, Observer {
            viewModel.updateMazeSeed(it)
            navigateToMaze()
        })
        binding.apply {
            textView2.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
            button.visibility = View.GONE
        }
    }

}