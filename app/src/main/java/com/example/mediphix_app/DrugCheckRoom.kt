package com.example.mediphix_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediphix_app.databinding.DrugCheckRoomPageBinding
import com.example.mediphix_app.drugs.DrugsAdapter
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class DrugCheckRoom : Fragment(R.layout.drug_check_room_page) {

    private var _binding: DrugCheckRoomPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private val drugList = mutableListOf<Drugs>()
    private lateinit var adapter: DrugsAdapter

    private var originalDrugList = mutableListOf<Drugs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DrugCheckRoomPageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.NextBtn.setOnClickListener {
            if(originalDrugList.isNullOrEmpty()){
                Toast.makeText(requireContext(), "No Drugs Available", Toast.LENGTH_SHORT).show()
            } else {
                val action = DrugCheckRoomDirections.actionDrugCheckRoomToDrugCheckFinalize()
                findNavController().navigate(action)
            }
        }

        binding.backBtn.setOnClickListener {
            val medTrack = requireActivity().application as MedTrack
            medTrack.roomDrugList.clear()
            val action = DrugCheckRoomDirections.actionDrugCheckRoomToDrugCheck()
            findNavController().navigate(action)
        }

        database = FirebaseDatabase.getInstance().getReference("Drugs")

        adapter = DrugsAdapter(drugList, true, object : DrugsAdapter.OnDrugClickListener {
            override fun onDrugClick(markedDrugList: MutableList<Drugs>) {
                // Call the function in ListOfDrugs from here
                updateRoomDrugList(markedDrugList)
            }
        })

        binding.recyclerViewRoomDrugs.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewRoomDrugs.adapter = adapter

        val medTrack = requireActivity().application as MedTrack
        var roomDrugList = medTrack.roomDrugList
        var selectedRoomForCheck = medTrack.selectedRoomForCheck.toString()
        binding.roomName.text = selectedRoomForCheck.split(" ")[0]
        binding.roomNam3.text = selectedRoomForCheck.split(" ")[1]

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
                    Toast.makeText(requireContext(), "No Drugs Available", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
            }
        })
        return root
    }

    private fun updateRoomDrugList(markedDrugList: MutableList<Drugs>) {
        val medTrack = requireActivity().application as MedTrack
        medTrack.roomDrugList = markedDrugList
    }

    private fun defaultSortFilterDrugList() {
        val medTrack = requireActivity().application as MedTrack
        var roomDrugList = medTrack.roomDrugList
        var selectedRoomForCheck = medTrack.selectedRoomForCheck

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate: Date = sdf.parse(sdf.format(Date()))!!
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_YEAR, 30)
        val futureDate: Date = calendar.time

        val filteredList = mutableListOf<Drugs>()

        if(roomDrugList.isEmpty() || roomDrugList[0]?.storageLocation.toString() != selectedRoomForCheck) {
            for (drug in originalDrugList) {
                if (drug.storageLocation.toString() == selectedRoomForCheck && (drug.drugLabel.toString() == "2" || drug.drugLabel.toString() == "1")) {

                    val expiredDate: Date = sdf.parse(drug.expiryDate)!!

                    if(expiredDate.before(currentDate)){
                        drug.drugLabel = 3;
                    }
                    else if(expiredDate.after(futureDate)){
                        drug.drugLabel = 1;
                    }
                    else {
                        drug.drugLabel = 2;
                    }
                    filteredList.add(drug)
                }
            }
            filteredList.sortByDescending {it.expiryDate}
            originalDrugList = filteredList
            roomDrugList = filteredList
            medTrack.roomDrugList = filteredList
        }

        adapter.updateList(roomDrugList)
    }
}