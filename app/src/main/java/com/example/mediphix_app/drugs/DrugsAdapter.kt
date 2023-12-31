package com.example.mediphix_app.drugs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mediphix_app.Drugs
import com.example.mediphix_app.R
import java.text.SimpleDateFormat
import java.util.*

class DrugsAdapter (private var drugList: MutableList<Drugs>,
                    private var isDrugCheckRoom: Boolean = false,
                    private val onDrugClickListener: OnDrugClickListener) : RecyclerView.Adapter<DrugsAdapter.ViewHolder>()  {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val drugName: TextView = itemView.findViewById(R.id.drug_name)
        val drugType: TextView = itemView.findViewById(R.id.drug_type)
        val drugId: TextView = itemView.findViewById(R.id.drug_number)
        val drugExpiry: TextView = itemView.findViewById(R.id.drug_expiry)
        val drugLabel: ImageView = itemView.findViewById(R.id.drugLabelImageView)
    }

    interface OnDrugClickListener {
        fun onDrugClick(markedDrugList: MutableList<Drugs>)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.drug_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var currentDrugInfo = drugList[position]

        holder.drugName.text = currentDrugInfo.name.toString().uppercase()
        holder.drugType.text = "TYPE: " + currentDrugInfo.drugType
        holder.drugId.text = "ID: " + currentDrugInfo.id;
        holder.drugExpiry.text = currentDrugInfo.expiryDate

        if(currentDrugInfo.drugLabel.toString() == "1"){ // Drug Label == 1 is for no marking
            holder.drugLabel.setImageResource(R.drawable.ic_no_icon)
        }
        else if (currentDrugInfo.drugLabel.toString() == "2"){ // Drug Label == 2 is for Red label marking
            holder.drugLabel.setImageResource(R.drawable.ic_red_label)

        }
        else if (currentDrugInfo.drugLabel.toString() == "3"){ // Drug Label == 3 is for Dispose/Expired label marking
            holder.drugLabel.setImageResource(R.drawable.ic_dispose)
        }

        val button: Button = holder.itemView.findViewById(R.id.spinner_drug_item)

        if(isDrugCheckRoom) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val currentDate: Date = sdf.parse(sdf.format(Date()))!!
            val calendar = Calendar.getInstance()
            calendar.time = currentDate
            calendar.add(Calendar.DAY_OF_YEAR, 30)
            val futureDate: Date = calendar.time

            val expiredDate: Date = sdf.parse(currentDrugInfo.expiryDate)!!

            if(expiredDate.before(currentDate) || expiredDate.before(futureDate)){
                holder.drugExpiry.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.mediphix_red))
            }

            button.setOnClickListener {
                if(currentDrugInfo.drugLabel.toString() == "1"){ // Drug Label == 1 is for no marking
                    holder.drugLabel.setImageResource(R.drawable.ic_red_label)
                    drugList[position].drugLabel = 2
                }
                else if (currentDrugInfo.drugLabel.toString() == "2"){ // Drug Label == 2 is for Red label marking
                    holder.drugLabel.setImageResource(R.drawable.ic_dispose)
                    drugList[position].drugLabel = 3
                }
                else if (currentDrugInfo.drugLabel.toString() == "3"){ // Drug Label == 3 is for Dispose/Expired label marking
                    holder.drugLabel.setImageResource(R.drawable.ic_no_icon)
                    drugList[position].drugLabel = 1
                }
                onDrugClickListener.onDrugClick(drugList)
            }
        }
    }

    fun updateList(newList: MutableList<Drugs>) {
        drugList = newList.toMutableList();
        notifyDataSetChanged()
    }

    override fun getItemCount() = drugList.size
}