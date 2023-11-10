package com.example.mediphix_app

import com.google.firebase.database.PropertyName

data class Checks(val regNumber : String? = null,
                  val nurse_first_name : String? = null,
                  val nurse_last_name : String? = null,
                  val storage_location : String? = null,
                  val checkDate : String? = null,
                  val imageUrl : String? = null,
                  val drugList : List<Drugs>? = null){}