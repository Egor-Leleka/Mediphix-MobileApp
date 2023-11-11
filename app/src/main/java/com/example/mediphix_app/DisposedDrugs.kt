package com.example.mediphix_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediphix_app.databinding.DisposedDrugsPageBinding
import com.example.mediphix_app.drugs.DisposedDrugsCheckAdapter
import com.google.firebase.database.*

class DisposedDrugs : Fragment(R.layout.disposed_drugs_page) {
    enum class FilterOptions {
        All, Simple, Controlled
    }

    private var _binding: DisposedDrugsPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private val disposedDrugsCheckList = mutableListOf<DisposedDrugsCheck>()
    private lateinit var adapter: DisposedDrugsCheckAdapter
    private var currentFilter: FilterOptions = FilterOptions.All

    private val originalCheckList = mutableListOf<Checks>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DisposedDrugsPageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        database = FirebaseDatabase.getInstance().getReference("Checks")

        adapter = DisposedDrugsCheckAdapter(disposedDrugsCheckList)
        binding.recyclerViewDrugs.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewDrugs.adapter = adapter

        val spinner: Spinner = binding.filterDDL
        val imageView: ImageView = binding.filterImageView

        imageView.bringToFront()
        imageView.setOnClickListener { spinner.performClick() }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> currentFilter = FilterOptions.All
                    1 -> currentFilter = FilterOptions.Simple
                    2 -> currentFilter = FilterOptions.Controlled
                }
                createDisposedDrugsCheckList(originalCheckList)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing here
            }
        }

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                disposedDrugsCheckList.clear()
                originalCheckList.clear()

                if (dataSnapshot.exists()) {
                    for (snapshot in dataSnapshot.children) {
                        val regNumber = snapshot.child("regNumber").value.toString()
                        val firstName = snapshot.child("nurse_first_name").value.toString()
                        val lastName = snapshot.child("nurse_last_name").value.toString()
                        val storageLocation = snapshot.child("storage_location").value.toString()
                        val imageUrl = snapshot.child("imageUrl").value.toString()
                        val drugsDataList = snapshot.child("drugList")

                        val currentDrugList = mutableListOf<Drugs>() // Create a new list for each check

                        for (drug in drugsDataList.children) {
                            val drugItem = Drugs(
                                name = drug.child("name").value.toString(),
                                id = drug.child("id").value.toString(),
                                drugType = drug.child("drugType").value.toString(),
                                securityType = drug.child("securityType").value.toString(),
                                storageLocation = drug.child("storageLocation").value.toString(),
                                expiryDate = drug.child("expiryDate").value.toString(),
                                drugLabel = (drug.child("drugLabel").value as Long?) ?: 3L // Default to 1L if null
                            )
                            currentDrugList.add(drugItem)
                        }

                        val checkDate = snapshot.child("checkDate").value.toString()

                        originalCheckList.add(Checks(regNumber, firstName, lastName, storageLocation, checkDate, imageUrl, currentDrugList))
                    }
                    createDisposedDrugsCheckList(originalCheckList)

                } else {
                    Toast.makeText(requireContext(), "No Checks Info", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
            }
        })
        return root
    }

    fun createDisposedDrugsCheckList(checksList: List<Checks>) {
        val filteredChecks = checksList.filter { it.drugList.isNullOrEmpty().not() }
        val groupedChecks = filteredChecks.groupBy { it.checkDate }

        val updatedDisposedDrugCheckList = groupedChecks.entries.sortedByDescending { it.key }.mapNotNull { group ->
            val checkDate = group.key ?: return@mapNotNull null

            var combinedDrugsList = group.value
                .flatMap { it.drugList.orEmpty() }
                .distinctBy { it.id }
                .mapNotNull { drug ->
                    if (drug.name == null || drug.id == null) return@mapNotNull null
                    DisposedDrug(
                        name = drug.name,
                        id = drug.id,
                        drugType = drug.drugType,
                        storageLocation = drug.storageLocation,
                        expiryDate = drug.expiryDate,
                        nurseName = "${group.value.first().nurse_first_name} ${group.value.first().nurse_last_name}"
                    )
                }
            combinedDrugsList = filterDrugList(combinedDrugsList)
            if (combinedDrugsList.isNotEmpty()) {
                DisposedDrugsCheck(
                    checkDate = checkDate,
                    disposedDrugsList = combinedDrugsList
                )
            } else null
        }

        adapter.updateList(updatedDisposedDrugCheckList as MutableList<DisposedDrugsCheck>)
    }


    private fun filterDrugList(disposedDrugList: List<DisposedDrug>) : List<DisposedDrug> {
        val filteredList = mutableListOf<DisposedDrug>()

        for (drug in disposedDrugList) {
            val drugType = drug.drugType

            when (currentFilter) {
                FilterOptions.All -> {
                    filteredList.add(drug)
                }
                FilterOptions.Simple -> {
                    if (drugType.toString() == "Simple") {
                        filteredList.add(drug)
                    }
                }
                FilterOptions.Controlled -> {
                    if (drugType.toString() == "Controlled") {
                        filteredList.add(drug)
                    }
                }
            }
        }
        return filteredList
    }
}