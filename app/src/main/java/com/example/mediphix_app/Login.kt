package com.example.mediphix_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mediphix_app.databinding.LoginPageBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Login : Fragment(R.layout.login_page) {

    private lateinit var binding: LoginPageBinding
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = LoginPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerBtn.setOnClickListener {
            val action = LoginDirections.loginToRegister()
            findNavController().navigate(action)
        }

        // Bind the Login data button
        binding.loginBtn.setOnClickListener {

            // Get the user's username
            val regNum: String = binding.regNumber.text.toString()
            val pass: String = binding.password.text.toString()

            if (regNum.isNotEmpty() && pass.isNotEmpty()) {
                // Verify username matches the password
                checkData(regNum, pass)
            } else {
                // Notify user if the field is empty
                Toast.makeText(requireContext(), "Please enter the Username and Password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkData(regNum: String, pass: String) {
        // Initialize Firebase database
        database = FirebaseDatabase.getInstance().getReference("Nurses")

        // Read user data from the database
        database.child(regNum).get().addOnSuccessListener {
            if (it.exists()) {

                // If user exists
                val regNumber = it.child("regNumber").value
                val password = it.child("password").value
                val firstName = it.child("firstName").value

                database.child(pass).get().addOnSuccessListener {
                    if (pass == password) {
                        // Success
                        Toast.makeText(requireContext(), "Successfully Logged-In", Toast.LENGTH_SHORT).show()

                        // Use NavController to navigate to the next destination
                        val action = LoginDirections.loginAction()
                        findNavController().navigate(action)
                    } else {
                        // No matching user
                        Toast.makeText(requireContext(), "Password Is Incorrect", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // No matching user
                Toast.makeText(requireContext(), "User Doesn't Exist", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            // Failure message = The database operation failed
            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
        }
    }
}