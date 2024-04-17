package com.gb.staymanager.Models

data class DepositSalary(
    val date : String,
    val amount : String,
    val isCash : Boolean,
    val isOnline : Boolean,
    val id : String
)
