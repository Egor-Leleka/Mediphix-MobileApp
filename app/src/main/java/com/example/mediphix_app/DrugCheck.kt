package com.example.mediphix_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mediphix_app.databinding.DrugCheckPageBinding
import com.google.firebase.database.*

class DrugCheck : Fragment(R.layout.drug_check_page) {
    private lateinit var binding: DrugCheckPageBinding
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DrugCheckPageBinding.inflate(inflater, container, false)

        database = FirebaseDatabase.getInstance().getReference("Checks")

        // Query to order checks by checkDate
        database.orderByChild("checkDate").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val latestCheck = filterChecksByLocation(dataSnapshot, "Knox Wing").lastOrNull()
                latestCheck?.let { check ->
                    binding.knoxTextView.text = "Last check: ${check.checkDate.toString()} \n Done by: ${check.nurse_first_name.toString().capitalizeFirstLetter()} ${check.nurse_last_name.toString().capitalizeFirstLetter()}"
                }

                val latestCheck1 = filterChecksByLocation(dataSnapshot, "Day Stay").lastOrNull()
                latestCheck1?.let { check ->
                    binding.dayTextView.text = "Last check: ${check.checkDate.toString()} \n Done by: ${check.nurse_first_name.toString().capitalizeFirstLetter()} ${check.nurse_last_name.toString().capitalizeFirstLetter()}"
                }

                val latestCheck2 = filterChecksByLocation(dataSnapshot, "Ward Office").lastOrNull()
                latestCheck2?.let { check ->
                    binding.wardTextView.text = "Last check: ${check.checkDate.toString()} \n Done by: ${check.nurse_first_name.toString().capitalizeFirstLetter()} ${check.nurse_last_name.toString().capitalizeFirstLetter()}"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })

        return binding.root
    }

    fun String.capitalizeFirstLetter(): String {
        if (this.isEmpty()) return this
        return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    fun filterChecksByLocation(dataSnapshot: DataSnapshot, locationFilter: String): List<Checks> {
        val checksList = mutableListOf<Checks>()

        dataSnapshot.children.forEach { snapshot ->
            // Extract the storage location from the key if the format allows
            val parts = snapshot.key?.split(" - ") ?: listOf()
            if (parts.size >= 3) {
                val storageLocation = parts[2]
                if (storageLocation.equals(locationFilter, ignoreCase = true)) {
                    // Manually parse the Checks object
                    val regNumber = snapshot.child("regNumber").value as String? ?: ""
                    val nurseFirstName = snapshot.child("nurse_first_name").value as String? ?: ""
                    val nurseLastName = snapshot.child("nurse_last_name").value as String? ?: ""
                    val storageLocation = snapshot.child("storage_Location").value as String? ?: ""
                    val checkDate = snapshot.child("checkDate").value as String? ?: ""
                    val imageUrl = snapshot.child("imageUrl").value as String? ?: ""

                    // Parse the drugList
                    val drugsSnapshot = snapshot.child("drugList")
                    val drugList = mutableListOf<Drugs>()
                    drugsSnapshot.children.forEach { drugSnapshot ->
                        val name = drugSnapshot.child("name").value as String? ?: ""
                        val id = drugSnapshot.child("id").value as String? ?: ""
                        val drugType = drugSnapshot.child("drugType").value as String? ?: ""
                        val securityType = drugSnapshot.child("securityType").value as String? ?: ""
                        val storageLocationDrug = drugSnapshot.child("storageLocation").value as String? ?: ""
                        val expiryDate = drugSnapshot.child("expiryDate").value as String? ?: ""
                        val drugLabel = drugSnapshot.child("drugLabel").value as Long? ?: 1L

                        drugList.add(Drugs(name, id, drugType, securityType, storageLocationDrug, expiryDate, drugLabel))
                    }

                    checksList.add(Checks(regNumber, nurseFirstName, nurseLastName, storageLocation, checkDate, imageUrl, drugList))
                }
            }
        }

        return checksList
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