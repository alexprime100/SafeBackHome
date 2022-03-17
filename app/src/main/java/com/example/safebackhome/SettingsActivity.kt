package com.example.safebackhome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        editPassword= findViewById(R.id.editTextTextPassword)
        editPin= findViewById(R.id.editTextNumberPassword)
        alertMessage= findViewById(R.id.editAlertMessage)
        //alertMessage.setText("cc")
    }
    private lateinit var alertMessage: EditText
    private lateinit var editPassword: EditText
    private lateinit var editPin: EditText
}

