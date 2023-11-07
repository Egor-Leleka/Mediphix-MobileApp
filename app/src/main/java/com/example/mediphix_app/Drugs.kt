package com.example.mediphix_app

data class Drugs(val name : String? = null,
                 val id : String? = null,
                 val drugType : String? = null,
                 val securityType : String? = null,
                 val storageLocation: String? = null,
                 val expiryDate: String? = null,
                 var drugLabel: Long = 1 // 1 = No Label, 2 = Red Sticker, 3 = Removed/Disposed/Expired
){}