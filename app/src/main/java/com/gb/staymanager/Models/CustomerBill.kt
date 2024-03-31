package com.gb.staymanager.Models

data class CustomerBill(
    val date : String,
    val customerName : String,
    val phone : String,
    val noOfPeople : String,
    val aadhaarNo : String,
    val amount : String,
    val isCash : Boolean,
    val isOnline : Boolean,
    val roomNo : String,
    val source : String
)
