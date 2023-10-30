package com.example.mediphix_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mediphix_app.databinding.RegisterPageBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Register : Fragment(R.layout.register_page) {

    private lateinit var binding: RegisterPageBinding
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = RegisterPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginBtn.setOnClickListener {
            val action = RegisterDirections.actionRegisterToLogin()
            findNavController().navigate(action)
        }

        // bind the register button
        binding.registerBtn.setOnClickListener {

            // Get user input from EditText fields
            val firstName = binding.firstName.text.toString()
            val lastName = binding.lastName.text.toString()
            val regNumber = binding.regNumber.text.toString()
            val password = binding.password.text.toString()

            if (password.length >= 8 && password.length <= 12) {
                // Create Firebase database reference
                database = FirebaseDatabase.getInstance().getReference("Nurses")

                // Create a Nurse instance
                val nurses = Nurses(firstName, lastName, regNumber, password)

                // Save the user object to the database using the username as the key
                database.child(regNumber).setValue(nurses).addOnSuccessListener {

                    // Clear input fields after successful database operation
                    binding.firstName.text.clear()
                    binding.lastName.text.clear()
                    binding.regNumber.text.clear()
                    binding.password.text.clear()

                    // Success message
                    Toast.makeText(requireContext(), "Nurse successfully saved", Toast.LENGTH_SHORT).show()

                }.addOnFailureListener {

                    // Failure message
                    Toast.makeText(requireContext(), "Failed to save new nurse", Toast.LENGTH_SHORT).show()
                }
            }
            else if (password.length >= 12){
                Toast.makeText(requireContext(), "Password is too long", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(requireContext(), "Password is too short", Toast.LENGTH_SHORT).show()
            }
        }
    }
}