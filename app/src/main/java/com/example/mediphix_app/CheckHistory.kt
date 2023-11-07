package com.example.mediphix_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediphix_app.checks.ChecksAdapter
import com.example.mediphix_app.databinding.CheckHistoryPageBinding
import com.google.firebase.database.*

class CheckHistory : Fragment(R.layout.check_history_page)  {
    private var _binding: CheckHistoryPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private var checkList = mutableListOf<Checks>()
    private lateinit var adapter: ChecksAdapter

    private val originalCheckList = mutableListOf<Checks>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = CheckHistoryPageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        database = FirebaseDatabase.getInstance().getReference("Checks")

        // Fetch the profile data for the currently logged-in user
        val nurseData = requireActivity().application as MedTrack
        val currentNurse = nurseData.currentNurseDetail

        adapter = ChecksAdapter(checkList)
        binding.recyclerViewDrugs.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewDrugs.adapter = adapter


        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                checkList.clear()
                originalCheckList.clear()

                if (dataSnapshot.exists()) {
                    for (snapshot in dataSnapshot.children) {
                        val regNumber = snapshot.child("regNumber").value.toString()
                        val Nurse_name = snapshot.child("Nurse_name").value.toString()
                        val Storage_Location = snapshot.child("Storage_Location").value.toString()

                        val drugList = snapshot.child("drugList").value.toString()
                        val checkDate = snapshot.child("checkDate").value.toString()

                        if (currentNurse != null && (currentNurse.regNumber == regNumber)){
                            originalCheckList.add(Checks(regNumber, Nurse_name, Storage_Location, drugList, checkDate))
                        }

                    }
                    adapter.updateList(originalCheckList)
                } else {
                    Toast.makeText(requireContext(), "No Drugs Info", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
            }
        })
        return root
    }

}