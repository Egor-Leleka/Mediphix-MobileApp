package com.example.mediphix_app

data class DisposedDrugsCheck (
    val checkDate : String? = null,
    val disposedDrugsList : List<DisposedDrug>? = null
) {}