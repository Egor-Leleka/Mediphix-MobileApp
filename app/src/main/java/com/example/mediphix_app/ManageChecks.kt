package com.example.mediphix_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediphix_app.checks.ChecksAdapter
import com.example.mediphix_app.databinding.ManageChecksPageBinding
import com.google.firebase.database.*

class ManageChecks : Fragment(R.layout.manage_checks_page) {

    private var _binding: ManageChecksPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private var checkList = mutableListOf<Checks>()
    private var drugList = mutableListOf<Drugs>()
    private lateinit var adapter: ChecksAdapter

    private val originalCheckList = mutableListOf<Checks>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = ManageChecksPageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        database = FirebaseDatabase.getInstance().getReference("Checks")

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
                        val firstName = snapshot.child("nurse_first_name").value.toString()
                        val lastName = snapshot.child("nurse_last_name").value.toString()
                        val storageLocation = snapshot.child("storage_Location").value.toString()

                        val drugsDataList = snapshot.child("drugList")
                        for (drug in drugsDataList.children){
                            val drugItem = Drugs(
                                name = drug.child("name").toString(),
                                id = drug.child("id").toString(),
                                drugType = drug.child("drugType").toString(),
                                securityType = drug.child("securityType").toString(),
                                storageLocation = drug.child("storageLocation").toString(),
                                expiryDate = drug.child("expiryDate").toString(),
                                drugLabel = drug.child("drugLabel").value as Long
                            )
                            drugList.add(drugItem)
                        }

                        val checkDate = snapshot.child("checkDate").value.toString()

                        originalCheckList.add(Checks(regNumber, firstName, lastName, storageLocation, checkDate, drugList))
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