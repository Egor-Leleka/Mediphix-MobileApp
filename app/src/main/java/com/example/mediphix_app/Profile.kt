package com.example.mediphix_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mediphix_app.databinding.ProfilePageBinding
import com.google.firebase.database.FirebaseDatabase

class Profile : Fragment(R.layout.profile_page) {
    private lateinit var binding: ProfilePageBinding

    // Firebase reference
    private val databaseReference = FirebaseDatabase.getInstance().getReference("Nurses")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = ProfilePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.checkHistoryBtn.setOnClickListener {
            val action = ProfileDirections.actionProfileToCheckHistory()
            findNavController().navigate(action)
        }

        binding.logoutBtn.setOnClickListener {
            val action = ProfileDirections.actionProfileToLogin()
            findNavController().navigate(action)

            val medTrack = requireActivity().application as MedTrack
            medTrack.roomDrugList.clear()
            medTrack.currentNurseDetail = null
        }

        // Fetch the profile data for the currently logged-in user
        val nurseApp = requireActivity().application as MedTrack
        val currentNurse = nurseApp.currentNurseDetail

        if (currentNurse != null) {
            binding.firstName.setText(currentNurse.firstName.toString().uppercase())
        }
        if (currentNurse != null) {
            binding.lastName.setText(currentNurse.lastName.toString().uppercase())
        }
        if (currentNurse != null) {
            binding.regNumber.setText(currentNurse.regNumber.toString().uppercase())
        }
    }
}