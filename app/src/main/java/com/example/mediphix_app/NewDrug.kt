package com.example.mediphix_app

import android.widget.DatePicker
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mediphix_app.databinding.AddNewDrugPageBinding
import com.example.mediphix_app.databinding.RegisterPageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*


class NewDrug : Fragment(R.layout.add_new_drug_page) {
    private lateinit var binding: AddNewDrugPageBinding
    private lateinit var database: DatabaseReference
    private lateinit var drugRoom: String
    private lateinit var drugExpiry: EditText

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

        drugExpiry = view.findViewById(R.id.drugExpiry)

        drugExpiry.setOnClickListener {
            showDatePickerDialog()
        }

        val spinner = view.findViewById<Spinner>(R.id.drugStorage)
        val drugSecurity = view.findViewById<EditText>(R.id.drugSecurity)
        val drugType = view.findViewById<EditText>(R.id.drugType)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                drugRoom = parent.getItemAtPosition(position).toString()

                when(drugRoom) {
                    "Knox Wing" -> {
                        drugSecurity.setText("Safe")
                        drugType.setText("Controlled")
                    }
                    "Day Stay", "Ward Office" -> {
                        drugSecurity.setText("Cupboard")
                        drugType.setText("Simple")
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        // bind the register button
        binding.registerDrugBtn.setOnClickListener {

            // Get user input from EditText fields
            val name = binding.DrugName.text.toString()
            val id = binding.idNumber.text.toString()
            val drugType = binding.drugType.text.toString()
            val securityType = binding.drugSecurity.text.toString()
            val storageLocation = drugRoom
            val expiryDate = binding.drugExpiry.text.toString()

            database = FirebaseDatabase.getInstance().getReference("Drugs")

            val drugs = Drugs(name, id, drugType, securityType, storageLocation, expiryDate)

            database.child(id).setValue(drugs).addOnSuccessListener {

                binding.DrugName.text.clear()
                binding.idNumber.text.clear()
                binding.drugType.text.clear()
                binding.drugSecurity.text.clear()
                spinner.setSelection(0)
                binding.drugExpiry.text.clear()

                // Success message
                Toast.makeText(requireContext(), "Drug successfully saved", Toast.LENGTH_SHORT).show()

            }.addOnFailureListener {

                // Failure message
                Toast.makeText(requireContext(), "Failed to save new drug", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = context?.let {
            DatePickerDialog(
                it,
                DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    // Month in DatePickerDialog is 0-indexed, so add 1 to month
                    val selectedDate = "$dayOfMonth/${month + 1}/$year"
                    drugExpiry.setText(selectedDate)
                },
                year,
                month,
                day
            )
        }

        // Set the minimum date to today to prevent selection of previous dates
        if (datePickerDialog != null) {
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        }

        datePickerDialog?.show()
    }
}