package com.example.mediphix_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class DisclaimerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.sleep(1000)
        installSplashScreen()

        setContentView(R.layout.activity_disclaimer)

        val checkBox: CheckBox = findViewById(R.id.check_agree)
        val nextButton: Button = findViewById(R.id.NextBtn)

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            nextButton.isEnabled = isChecked
        }

        nextButton.setOnClickListener {
            if (checkBox.isChecked) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else {
                Toast.makeText(this, "Please tick the checkbox before proceeding", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
