package com.example.mediphix_app

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediphix_app.databinding.DrugCheckFinalizePageBinding
import com.example.mediphix_app.drugs.DrugsAdapter
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class DrugCheckFinalize : Fragment(R.layout.drug_check_finalize_page) {

    private var _binding: DrugCheckFinalizePageBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private val drugList = mutableListOf<Drugs>()
    private lateinit var redStickerDrugAdapter: DrugsAdapter
    private lateinit var removedDrugAdapter: DrugsAdapter
    private val redStickerDrugList = mutableListOf<Drugs>()
    private val removedDrugList = mutableListOf<Drugs>()
    private var imageUrl : String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DrugCheckFinalizePageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.backBtn.setOnClickListener {
            val action = DrugCheckFinalizeDirections.actionDrugCheckFinalizeToDrugCheckRoom()
            findNavController().navigate(action)
        }

        database = FirebaseDatabase.getInstance().getReference("Drugs")

        redStickerDrugAdapter = DrugsAdapter(drugList, false, object : DrugsAdapter.OnDrugClickListener {
            override fun onDrugClick(markedDrugList: MutableList<Drugs>) {
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_LONG).show()
            }
        })
        binding.recyclerViewFinalizeRoomDrugs1.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFinalizeRoomDrugs1.adapter = redStickerDrugAdapter

        removedDrugAdapter = DrugsAdapter(drugList, false, object : DrugsAdapter.OnDrugClickListener {
            override fun onDrugClick(markedDrugList: MutableList<Drugs>) {
                // Do nothing here
            }
        })
        binding.recyclerViewFinalizeRoomDrugs2.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFinalizeRoomDrugs2.adapter = removedDrugAdapter

        val medTrack = requireActivity().application as MedTrack
        var roomDrugList = medTrack.roomDrugList
        var selectedRoomForCheck = medTrack.selectedRoomForCheck.toString().uppercase()
        var nurseData = medTrack.currentNurseDetail
        binding.roomName.text = "DRUG CHECK - $selectedRoomForCheck"
        binding.nurseFullNameTextView.text = "NURSE NAME: ${nurseData?.firstName.toString().uppercase()} ${nurseData?.lastName.toString().uppercase()}"
        binding.nurseRegNoTextView.text = "REG NO.: ${nurseData?.regNumber.toString()}"

        defaultSortFilterDrugList()

        if (redStickerDrugList.isNullOrEmpty() && removedDrugList.isNullOrEmpty()){
            Toast.makeText(requireContext(), "No Drugs Available", Toast.LENGTH_SHORT).show()
        }

        binding.SaveBtn.setOnClickListener {

            binding.myProgressBar.visibility = View.VISIBLE
            it.isEnabled = false
            binding.backBtn.isEnabled = false

            if (redStickerDrugList.isNullOrEmpty() && removedDrugList.isNullOrEmpty()){
                Toast.makeText(requireContext(), "No Drugs Available", Toast.LENGTH_SHORT).show()
            }
            else
            {
                database = FirebaseDatabase.getInstance().getReference("Drugs")

                val drugChildren = hashMapOf<String, Any>()

                roomDrugList.forEach { updatedDrug ->
                    val key = updatedDrug.id
                    if (key != null) {
                        drugChildren["$key/drugLabel"] = updatedDrug.drugLabel
                    }
                }

                database.updateChildren(drugChildren).addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(requireContext(), "Failed to save Drugs", Toast.LENGTH_SHORT).show()
                    }
                }

                database = FirebaseDatabase.getInstance().getReference("Checks")

                val medTrack = requireActivity().application as MedTrack
                var roomDrugList = medTrack.roomDrugList
                var selectedRoomForCheck = medTrack.selectedRoomForCheck.toString().uppercase()
                var nurseData = medTrack.currentNurseDetail

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val sdf2 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val currentDate = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time

                val dateString = sdf.format(currentDate)
                val dateDashString = sdf2.format(currentDate)

                val bitmap = binding.signatureView.getSignatureBitmap()
                // Convert bitmap to byte array
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                val data = baos.toByteArray()

                // Save to Firebase
                val storageRef = FirebaseStorage.getInstance().reference
                val signatureRef = storageRef.child("signatures/${UUID.randomUUID()}.png")

                val uploadTask = signatureRef.putBytes(data)
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                    // It's a good practice to provide more detail on the error to the user or in the logs.
                    Toast.makeText(requireContext(), "Upload failed: ${it.message}", Toast.LENGTH_LONG).show()
                }.addOnSuccessListener { taskSnapshot ->
                    // Handle successful uploads on complete
                    // Get the download URL after the upload is successful
                    taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()

                        //record only disposed drugs
                        val filteredList = roomDrugList.filter { drug ->
                            drug.drugLabel == 3L
                        }

                        // Create the Checks object here after you have the URL
                        val check = Checks(
                            nurseData?.regNumber,
                            nurseData?.firstName,
                            nurseData?.lastName,
                            selectedRoomForCheck,
                            dateString.toString(),
                            imageUrl,
                            filteredList
                        )

                        // Save the Checks object to the database
                        database.child(dateDashString.toString() + " - " + nurseData?.regNumber.toString() + " - " + selectedRoomForCheck)
                            .setValue(check)
                            .addOnSuccessListener {
                                medTrack.roomDrugList.clear()
                                showPopUp()
                            }.addOnFailureListener {
                                // Handle the error properly
                                Toast.makeText(requireContext(), "Failed to save check: ${it.message}", Toast.LENGTH_LONG).show()
                            }
                    }?.addOnFailureListener {
                        // Handle any errors in getting the download URL
                        Toast.makeText(requireContext(), "Failed to get download URL: ${it.message}", Toast.LENGTH_LONG).show()
                    }
                }

            }

            hideLoading()
        }
        return root
    }


    private fun hideLoading() {
        binding.myProgressBar.visibility = View.GONE
        binding.backBtn.isEnabled = true
        binding.SaveBtn.isEnabled= true
    }

    private fun showPopUp() {
        val dialog = Dialog(requireContext())
        val view = layoutInflater.inflate(R.layout.save_message, null)

        view.setOnClickListener {
            dialog.dismiss()
            val action = DrugCheckFinalizeDirections.actionDrugCheckFinalizeToHome()
            findNavController().navigate(action)
        }

        dialog.setContentView(view)
        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#80000000")))
    }


    private fun defaultSortFilterDrugList() {
        val nurseApp = requireActivity().application as MedTrack
        val selectedRoomForCheck = nurseApp.selectedRoomForCheck

        val medTrack = requireActivity().application as MedTrack
        var roomDrugList = medTrack.roomDrugList

        roomDrugList.sortByDescending {it.drugLabel}

        for (drug in roomDrugList) {
            val storageLocation = drug.storageLocation
            if (storageLocation.toString() == selectedRoomForCheck && drug.drugLabel.toString() == "2") {
                redStickerDrugList.add(drug)
            }
            else if (storageLocation.toString() == selectedRoomForCheck && drug.drugLabel.toString() == "3") {
                removedDrugList.add(drug)
            }
        }
        redStickerDrugAdapter.updateList(redStickerDrugList)
        removedDrugAdapter.updateList(removedDrugList)
    }

}