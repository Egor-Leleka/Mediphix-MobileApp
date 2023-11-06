package com.example.mediphix_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mediphix_app.databinding.HomePageBinding

class Home : Fragment(R.layout.home_page) {

    private lateinit var binding: HomePageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = HomePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startDrugCheckBtn.setOnClickListener {
            val action = HomeDirections.actionHomeToDrugCheck()
            findNavController().navigate(action)
        }

        binding.listOfDrugsBtn.setOnClickListener {
            val action = HomeDirections.actionHomeToListOfDrugs()
            findNavController().navigate(action)
        }

        binding.disposedDrugsBtn.setOnClickListener {
            val action = HomeDirections.actionHomeToDisposedDrugs()
            findNavController().navigate(action)
        }

        binding.manageChecksBtn.setOnClickListener {
            val action = HomeDirections.actionHomeToManageChecks()
            findNavController().navigate(action)
        }

        binding.manageChecksBtn.setOnClickListener {
            val action = HomeDirections.actionHomeToAboutMediphix()
            findNavController().navigate(action)
        }
    }
}