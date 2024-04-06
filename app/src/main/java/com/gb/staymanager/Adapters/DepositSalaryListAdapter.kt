package com.gb.staymanager.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gb.staymanager.Models.DepositSalary
import com.gb.staymanager.databinding.IndivisualDepositBinding

class DepositSalaryListAdapter(private val context : Context, private val depositSalaryList : ArrayList<DepositSalary>) : RecyclerView.Adapter<DepositSalaryListAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding : IndivisualDepositBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = IndivisualDepositBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return depositSalaryList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.date.text = depositSalaryList[position].date
        holder.binding.amount.text = depositSalaryList[position].amount
        if(depositSalaryList[position].isCash){
           holder.binding.buttonCashOrOnline.text = "Cash"
        }
        else{
            holder.binding.buttonCashOrOnline.text = "Online"
        }
    }
}