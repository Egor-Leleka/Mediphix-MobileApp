package com.example.mediphix_app

data class Checks(val regNumber : String? = null,
                  val Nurse_first_name : String? = null,
                  val Nurse_last_name : String? = null,
                  val Storage_Location : String? = null,
                  val checkDate : String? = null,
                  val imageUrl : String? = null,
                  val drugList : List<Drugs>){}