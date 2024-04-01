package com.gb.staymanager.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gb.staymanager.Models.CustomerBill
import com.gb.staymanager.Models.DepositSalary
import com.gb.staymanager.databinding.IndivisualDepositBinding
import com.gb.staymanager.databinding.IndivisualReportItemBinding

class ReportListAdapter(private val context : Context, private val reportList : ArrayList<CustomerBill>) : RecyclerView.Adapter<ReportListAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding : IndivisualReportItemBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = IndivisualReportItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.roomNo.text = "Room : ${reportList[position].roomNo}"
        holder.binding.date.text = reportList[position].date
        holder.binding.amount.text = reportList[position].amount + "₹"
        if(reportList[position].isCash) holder.binding.buttonCashOrOnline.text = "Cash"
        if(reportList[position].isOnline) holder.binding.buttonCashOrOnline.text = "Online"
    }
}