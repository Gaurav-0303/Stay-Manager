package com.gb.staymanager.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gb.staymanager.databinding.IndivisualEmployeeBinding

class EmployeeListAdapter(private val context: Context, private val employeeList: ArrayList<Pair<String, String>>) : RecyclerView.Adapter<EmployeeListAdapter.MyViewHolder>() {

    private lateinit var itemClickListener: OnItemClickListener
    private lateinit var deleteIconClickListener: OnDeleteIconClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnDeleteIconClickListener {
        fun onDeleteIconClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }

    fun setOnDeleteIconClickListener(listener: OnDeleteIconClickListener) {
        this.deleteIconClickListener = listener
    }

    inner class MyViewHolder(val binding: IndivisualEmployeeBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            // Set click listener for the item view
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(position)
                }
            }

            // Set click listener for the delete icon
            binding.buttonDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    deleteIconClickListener.onDeleteIconClick(position)
                }
            }
        }

        fun bind(employee: Pair<String, String>) {
            binding.textViewEmployeeName.text = employee.first
            binding.textViewEmployeeNumber.text = employee.second
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = IndivisualEmployeeBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return employeeList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(employeeList[position])
    }
}
