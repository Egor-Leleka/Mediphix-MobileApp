package com.example.mediphix_app

import android.app.Application

class MedTrack : Application() {
    var currentNurseDetail: Nurses? = null
    var selectedRoomForCheck : String = "Knox Wing"
    var roomDrugList = mutableListOf<Drugs>()
}