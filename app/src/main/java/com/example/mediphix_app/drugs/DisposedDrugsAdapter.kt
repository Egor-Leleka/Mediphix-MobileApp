package com.example.mediphix_app.drugs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mediphix_app.Checks
import com.example.mediphix_app.DisposedDrug
import com.example.mediphix_app.Drugs
import com.example.mediphix_app.R
import java.text.SimpleDateFormat
import java.util.*

class DisposedDrugsAdapter (private var disposedDrugList: MutableList<DisposedDrug>) : RecyclerView.Adapter<DisposedDrugsAdapter.ViewHolder>()  {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val drugName: TextView = itemView.findViewById(R.id.drug_name)
        val drugType: TextView = itemView.findViewById(R.id.drug_type)
        val drugId: TextView = itemView.findViewById(R.id.drug_number)
        val drugExpiry: TextView = itemView.findViewById(R.id.drug_expiry)
        val nurseName: TextView = itemView.findViewById(R.id.nurse_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.disposed_drugs_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var currentDrugInfo = disposedDrugList[position]

        holder.drugName.text = currentDrugInfo.name.toString().uppercase()
        holder.drugType.text = "TYPE: " + currentDrugInfo.drugType.toString().uppercase()
        holder.drugId.text = "ID: " + currentDrugInfo.id.toString().uppercase()
        holder.drugExpiry.text = "EXPIRED: " + currentDrugInfo.expiryDate.toString().uppercase()
        holder.nurseName.text = "REMOVED BY: " + currentDrugInfo.nurseName.toString().uppercase()
    }

    fun updateList(newList: MutableList<DisposedDrug>) {
        disposedDrugList = newList.toMutableList();
        notifyDataSetChanged()
    }

    override fun getItemCount() = disposedDrugList.size
}