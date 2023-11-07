package com.example.mediphix_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mediphix_app.databinding.DrugCheckPageBinding

class DrugCheck : Fragment(R.layout.drug_check_page) {
    private lateinit var binding: DrugCheckPageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DrugCheckPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.knoxWingDrugRoomBtn.setOnClickListener {
            val nurseApp = requireActivity().application as MedTrack
            nurseApp.selectedRoomForCheck = "Knox Wing"

            val action = DrugCheckDirections.actionDrugCheckToDrugCheckRoom()
            findNavController().navigate(action)
        }
        binding.wardOfficeNursesStationBtn.setOnClickListener {
            val nurseApp = requireActivity().application as MedTrack
            nurseApp.selectedRoomForCheck = "Ward Office"

            val action = DrugCheckDirections.actionDrugCheckToDrugCheckRoom()
            findNavController().navigate(action)
        }
        binding.dayStayNursesStationBtn.setOnClickListener {
            val nurseApp = requireActivity().application as MedTrack
            nurseApp.selectedRoomForCheck = "Day Stay"

            val action = DrugCheckDirections.actionDrugCheckToDrugCheckRoom()
            findNavController().navigate(action)
        }
    }

}