package com.example.mediphix_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediphix_app.databinding.DrugCheckFinalizePageBinding
import com.example.mediphix_app.drugs.DrugsAdapter
import com.google.firebase.database.*

class DrugCheckFinalize : Fragment(R.layout.drug_check_finalize_page) {

    private var _binding: DrugCheckFinalizePageBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private val drugList = mutableListOf<Drugs>()
    private lateinit var redStickerDrugAdapter: DrugsAdapter
    private lateinit var removedDrugAdapter: DrugsAdapter

    private val originalDrugList = mutableListOf<Drugs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DrugCheckFinalizePageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.backBtn.setOnClickListener {
            val action = DrugCheckFinalizeDirections.actionDrugCheckFinalizeToDrugCheckRoom()
            findNavController().navigate(action)
        }

        database = FirebaseDatabase.getInstance().getReference("Drugs")

        redStickerDrugAdapter = DrugsAdapter(drugList)
        binding.recyclerViewFinalizeRoomDrugs1.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFinalizeRoomDrugs1.adapter = redStickerDrugAdapter

        removedDrugAdapter = DrugsAdapter(drugList)
        binding.recyclerViewFinalizeRoomDrugs2.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFinalizeRoomDrugs2.adapter = removedDrugAdapter

        val medTrack = requireActivity().application as MedTrack
        var roomDrugList = medTrack.roomDrugList
        var selectedRoomForCheck = medTrack.selectedRoomForCheck.toString().uppercase()
        var nurseData = medTrack.currentNurseDetail
        binding.roomName.text = "DRUG CHECK - $selectedRoomForCheck"
        binding.nurseFullNameTextView.text = "NURSE NAME: ${nurseData?.firstName.toString().uppercase()} ${nurseData?.lastName.toString().uppercase()}"
        binding.nurseRegNoTextView.text = "REG NO.: ${nurseData?.regNumber.toString()}"

        if (!roomDrugList.isNullOrEmpty()) {
            defaultSortFilterDrugList()
        } else {
            Toast.makeText(requireContext(), "No Drugs Info", Toast.LENGTH_SHORT).show()
        }
        /*
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                drugList.clear()
                originalDrugList.clear()

                if (dataSnapshot.exists()) {
                    for (snapshot in dataSnapshot.children) {
                        val drugType = snapshot.child("drugType").value.toString()
                        val expiryDate = snapshot.child("expiryDate").value.toString()
                        val drugId = snapshot.child("id").value.toString()
                        val drugName = snapshot.child("name").value.toString()
                        val securityType = snapshot.child("securityType").value.toString()
                        val storageLocation = snapshot.child("storageLocation").value.toString()
                        val drugLabel = snapshot.child("drugLabel").value as Long

                        originalDrugList.add(Drugs(drugName, drugId, drugType, securityType, storageLocation, expiryDate, drugLabel))
                    }
                    defaultSortFilterDrugList()
                } else {
                    Toast.makeText(requireContext(), "No Drugs Info", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
            }
        })*/
        return root
    }

    private fun defaultSortFilterDrugList() {
        val redStickerDrugList = mutableListOf<Drugs>()
        val removedDrugList = mutableListOf<Drugs>()

        val nurseApp = requireActivity().application as MedTrack
        val selectedRoomForCheck = nurseApp.selectedRoomForCheck

        val medTrack = requireActivity().application as MedTrack
        var roomDrugList = medTrack.roomDrugList

        roomDrugList.sortByDescending {it.drugLabel}

        for (drug in roomDrugList) {
            val storageLocation = drug.storageLocation
            if (storageLocation.toString() == selectedRoomForCheck && drug.drugLabel.toString() == "2") {
                redStickerDrugList.add(drug)
            }
            else if (storageLocation.toString() == selectedRoomForCheck && drug.drugLabel.toString() == "3") {
                removedDrugList.add(drug)
            }
        }

        redStickerDrugAdapter.updateList(redStickerDrugList)
        removedDrugAdapter.updateList(removedDrugList)
    }

}