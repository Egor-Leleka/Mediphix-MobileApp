package com.example.mediphix_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediphix_app.databinding.ListOfDrugsPageBinding
import com.example.mediphix_app.drugs.DrugsAdapter
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*


class ListOfDrugs : Fragment(R.layout.list_of_drugs_page) {
    enum class FilterOptions {
        All, Red_Sticker, Expired, Simple, Controlled
    }

    private var _binding: ListOfDrugsPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private val drugList = mutableListOf<Drugs>()
    private lateinit var adapter: DrugsAdapter
    private var currentFilter: FilterOptions = FilterOptions.All

    private val originalDrugList = mutableListOf<Drugs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = ListOfDrugsPageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        database = FirebaseDatabase.getInstance().getReference("Drugs")

        adapter = DrugsAdapter(drugList, false, object : DrugsAdapter.OnDrugClickListener {
            override fun onDrugClick(markedDrugList: MutableList<Drugs>) {
                // Do nothing here
            }
        })
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
                    1 -> currentFilter = FilterOptions.Red_Sticker
                    2 -> currentFilter = FilterOptions.Expired
                    3 -> currentFilter = FilterOptions.Simple
                    4 -> currentFilter = FilterOptions.Controlled
                }
                filterDrugList()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing here
            }
        }

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
                    adapter.updateList(originalDrugList)
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

    private fun filterDrugList() {
        val filteredList = mutableListOf<Drugs>()
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate: Date = sdf.parse(sdf.format(Date()))!!
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_YEAR, 30)
        val futureDate: Date = calendar.time

        for (drug in originalDrugList) {
            val drugType = drug.drugType
            val drugLabel = drug.drugLabel

            when (currentFilter) {
                FilterOptions.All -> {
                    filteredList.add(drug)
                }
                FilterOptions.Red_Sticker -> {
                    if (drugLabel.toString() == "2") {
                        filteredList.add(drug)
                    }
                }
                FilterOptions.Expired -> {
                    if (drugLabel.toString() == "3") {
                        filteredList.add(drug)
                    }
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
        adapter.updateList(filteredList)
    }
}


