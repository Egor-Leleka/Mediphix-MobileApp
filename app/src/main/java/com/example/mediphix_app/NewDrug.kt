package com.example.mediphix_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mediphix_app.databinding.AddNewDrugPageBinding
import com.example.mediphix_app.databinding.RegisterPageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class NewDrug : Fragment(R.layout.add_new_drug_page) {
    private lateinit var binding: AddNewDrugPageBinding
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = AddNewDrugPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // bind the register button
        binding.registerDrugBtn.setOnClickListener {

            // Get user input from EditText fields
            val name = binding.DrugName.text.toString()
            val id = binding.idNumber.text.toString()
            val drugType = binding.drugType.text.toString()
            val securityType = binding.drugSecurity.text.toString()
            val storageLocation = binding.drugStorage.text.toString()
            val expiryDate = binding.drugExpiry.text.toString()

            database = FirebaseDatabase.getInstance().getReference("Drugs")

            // Create a Nurse instance
            val drugs = Drugs(name, id, drugType, securityType, storageLocation, expiryDate)

            // Save the user object to the database using the username as the key
            database.child(id).setValue(drugs).addOnSuccessListener {

                // Clear input fields after successful database operation
                binding.DrugName.text.clear()
                binding.idNumber.text.clear()
                binding.drugType.text.clear()
                binding.drugSecurity.text.clear()
                binding.drugStorage.text.clear()
                binding.drugExpiry.text.clear()

                // Success message
                Toast.makeText(requireContext(), "Drug successfully saved", Toast.LENGTH_SHORT).show()

            }.addOnFailureListener {

                // Failure message
                Toast.makeText(requireContext(), "Failed to save new drug", Toast.LENGTH_SHORT).show()
            }
        }
    }
}