package com.puntogris.neonmaze.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
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
        viewModel.currentMaze.observe(viewLifecycleOwner, Observer {})

        return binding.root
    }


    private fun navigateToMazeFragment(){
        findNavController().navigate(R.id.action_welcomeFragment_to_mazeFragment)
    }

    fun fetchMazeInformationFromDatabase(){
        viewModel.apply {
            createPlayer()
            getMazeSeed().observe(viewLifecycleOwner, Observer { seed ->
                updateMazeSeed(seed)
                navigateToMazeFragment()
            })
        }

        binding.apply {
            loadingGroup.visibility = View.VISIBLE
            button.visibility = View.GONE
        }
    }

}