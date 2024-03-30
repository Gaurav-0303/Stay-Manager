package com.gb.staymanager.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gb.staymanager.databinding.IndivisualEmployeeBinding

class EmployeeListAdapter(private val context : Context, private val employeeList : ArrayList<Pair<String, String>>) : RecyclerView.Adapter<EmployeeListAdapter.MyViewHolder>() {

    private lateinit var MyListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnClickListener(listener: OnItemClickListener){
        MyListener = listener
    }

    inner class MyViewHolder(val binding : IndivisualEmployeeBinding,  listener: OnItemClickListener) : RecyclerView.ViewHolder(binding.root){
        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = IndivisualEmployeeBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding, MyListener)
    }

    override fun getItemCount(): Int {
        return employeeList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.textViewEmployeeName.text = employeeList[position].first
        holder.binding.textViewEmployeeNumber.text = employeeList[position].second
    }
}