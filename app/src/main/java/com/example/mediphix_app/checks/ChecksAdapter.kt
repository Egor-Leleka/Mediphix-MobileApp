package com.example.mediphix_app.checks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mediphix_app.Checks
import com.example.mediphix_app.R

class ChecksAdapter (private var checkList: MutableList<Checks>) : RecyclerView.Adapter<ChecksAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkDate: TextView = itemView.findViewById(R.id.checkDate)
        val storageLocation: TextView = itemView.findViewById(R.id.storageLocation)
        val nurseName: TextView = itemView.findViewById(R.id.nurseName)
        val regNo: TextView = itemView.findViewById(R.id.regNo)
        val drugList: TextView = itemView.findViewById(R.id.drugList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.checks_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentCheckInfo = checkList[position]
        holder.checkDate.text = "CHECK DATE: " + currentCheckInfo.checkDate.toString()
        holder.storageLocation.text ="Storage Location: " + currentCheckInfo.Storage_Location
        holder.nurseName.text ="Done by: "+ currentCheckInfo.Nurse_first_name.toString().uppercase() + " " + currentCheckInfo.Nurse_last_name.toString().uppercase()
        holder.regNo.text = "Reg No.: " +currentCheckInfo.regNumber

        var drugText = "Drug List:"
        for((i, drug) in currentCheckInfo.drugList.withIndex()) {
            val prefix = "value = "
            val suffix = " }"
            val drugIdString = drug.id.toString()
            val idStartIndex = drugIdString.indexOf(prefix) + prefix.length
            val idEndIndex = drugIdString.indexOf(suffix, startIndex = idStartIndex)
            val drugId = drugIdString.substring(idStartIndex, idEndIndex).trim()

            val drugNameString = drug.name.toString()
            val nameStartIndex = drugNameString.indexOf(prefix) + prefix.length
            val nameEndIndex = drugNameString.indexOf(suffix, startIndex = nameStartIndex)
            val drugName = drugNameString.substring(nameStartIndex, nameEndIndex).trim()
            drugText += "\n" + (i+1).toString() + ". " + drugId + " - " + drugName
        }

        holder.drugList.text = drugText


    }

    fun updateList(newList: MutableList<Checks>) {
        checkList = newList.toMutableList();
        notifyDataSetChanged()
    }

    override fun getItemCount() = checkList.size

}