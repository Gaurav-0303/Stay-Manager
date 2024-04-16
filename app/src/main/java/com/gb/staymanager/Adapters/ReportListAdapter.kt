package com.gb.staymanager.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gb.staymanager.Models.CustomerBill
import com.gb.staymanager.databinding.IndivisualReportItemBinding

class ReportListAdapter(private val context: Context, private val reportList: ArrayList<CustomerBill>) : RecyclerView.Adapter<ReportListAdapter.MyViewHolder>() {

    private lateinit var MyListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnClickListener(listener: OnItemClickListener){
        MyListener = listener
    }

    inner class MyViewHolder(val binding: IndivisualReportItemBinding,  listener: OnItemClickListener) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = IndivisualReportItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding, MyListener)
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.roomNo.text = "Room : ${reportList[position].roomNo}"
        holder.binding.date.text = reportList[position].date
        holder.binding.amount.text = reportList[position].amount + "₹"

        // Check if the transaction is cash or online
        if (reportList[position].isCash) {
            holder.binding.buttonCashOrOnline.text = "Cash"
        } else if (reportList[position].isOnline) {
            holder.binding.buttonCashOrOnline.text = "Online"
        }
    }

}
