package com.example.mediphix_app.drugs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediphix_app.DisposedDrug
import com.example.mediphix_app.DisposedDrugsCheck
import com.example.mediphix_app.R

class DisposedDrugsCheckAdapter (private var disposedDrugsList: MutableList<DisposedDrugsCheck>) : RecyclerView.Adapter<DisposedDrugsCheckAdapter.ViewHolder>()  {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkDate: TextView = itemView.findViewById(R.id.check_date_text_view)
        private val recyclerView : RecyclerView = itemView.findViewById(R.id.disposed_drugs_recycler_view)

        fun bind(disposedDrugs: DisposedDrugsCheck) {
            checkDate.text = disposedDrugs.checkDate
            recyclerView.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.VERTICAL, false)
            recyclerView.adapter = DisposedDrugsAdapter((disposedDrugs.disposedDrugsList ?: emptyList()) as MutableList<DisposedDrug>)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.disposed_drugs_check_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var currentCheckInfo = disposedDrugsList[position]
        holder.bind(currentCheckInfo)
    }

    fun updateList(newList: MutableList<DisposedDrugsCheck>) {
        disposedDrugsList = newList.toMutableList();
        notifyDataSetChanged()
    }

    override fun getItemCount() = disposedDrugsList.size
}