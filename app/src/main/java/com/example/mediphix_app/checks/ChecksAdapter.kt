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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecksAdapter.ViewHolder {
        return ChecksAdapter.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.checks_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChecksAdapter.ViewHolder, position: Int) {
        val currentCheckInfo = checkList[position]
        holder.checkDate.text = "Check Date :" + currentCheckInfo.checkDate.toString()
        holder.storageLocation.text ="Storage Location :" + currentCheckInfo.Storage_Location
        holder.nurseName.text ="Done by :"+ currentCheckInfo.Nurse_name
        holder.regNo.text = "Reg. No :" +currentCheckInfo.regNumber
        holder.drugList.text = "Drug List :" +currentCheckInfo.drugList


    }

    fun updateList(newList: MutableList<Checks>) {
        checkList = newList.toMutableList();
        notifyDataSetChanged()
    }

    override fun getItemCount() = checkList.size

}