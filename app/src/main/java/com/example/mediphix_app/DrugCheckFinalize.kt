package com.example.mediphix_app

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import java.text.SimpleDateFormat
import java.util.*

class DrugCheckFinalize : Fragment(R.layout.drug_check_finalize_page) {

    private var _binding: DrugCheckFinalizePageBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private val drugList = mutableListOf<Drugs>()
    private lateinit var redStickerDrugAdapter: DrugsAdapter
    private lateinit var removedDrugAdapter: DrugsAdapter

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

        redStickerDrugAdapter = DrugsAdapter(drugList, false, object : DrugsAdapter.OnDrugClickListener {
            override fun onDrugClick(markedDrugList: MutableList<Drugs>) {
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_LONG).show()
            }
        })
        binding.recyclerViewFinalizeRoomDrugs1.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFinalizeRoomDrugs1.adapter = redStickerDrugAdapter

        removedDrugAdapter = DrugsAdapter(drugList, false, object : DrugsAdapter.OnDrugClickListener {
            override fun onDrugClick(markedDrugList: MutableList<Drugs>) {
                // Do nothing here
            }
        })
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

        binding.SaveBtn.setOnClickListener {
            database = FirebaseDatabase.getInstance().getReference("Drugs")

            val drugChildren = hashMapOf<String, Any>()

            roomDrugList.forEach { updatedDrug ->
                val key = updatedDrug.id
                if (key != null) {
                    drugChildren["$key/drugLabel"] = updatedDrug.drugLabel
                }
            }

            database.updateChildren(drugChildren).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(requireContext(), "Failed to save Drugs", Toast.LENGTH_SHORT).show()
                }
            }

            database = FirebaseDatabase.getInstance().getReference("Checks")

            val medTrack = requireActivity().application as MedTrack
            var roomDrugList = medTrack.roomDrugList
            var selectedRoomForCheck = medTrack.selectedRoomForCheck.toString().uppercase()
            var nurseData = medTrack.currentNurseDetail

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val sdf2 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val dateString = sdf.format(currentDate)
            val dateDashString = sdf2.format(currentDate)

            val check = Checks(nurseData?.regNumber, nurseData?.firstName, nurseData?.lastName, selectedRoomForCheck, dateString.toString(), roomDrugList)

            database.child(dateDashString.toString() + " - " + nurseData?.regNumber.toString()).setValue(check).addOnSuccessListener {
                showPopUp()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to save check", Toast.LENGTH_SHORT).show()
            }
        }
        return root
    }

    private fun showPopUp() {
        val dialog = Dialog(requireContext())
        val view = layoutInflater.inflate(R.layout.save_message, null)

        view.setOnClickListener {
            dialog.dismiss()
            val action = DrugCheckFinalizeDirections.actionDrugCheckFinalizeToHome()
            findNavController().navigate(action)
        }

        dialog.setContentView(view)
        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#80000000")))
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