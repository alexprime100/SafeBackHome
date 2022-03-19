package com.example.safebackhome.activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.example.safebackhome.R
import com.example.safebackhome.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var welcomeMessage : TextView
    private lateinit var fireAuthentication : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var loggedUser : User
    private lateinit var settings: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        welcomeMessage = findViewById(R.id.name_textview)
        settings = findViewById(R.id.settings_button)

        settings.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                startActivity(Intent(applicationContext, SettingsActivity::class.java))
            }
        })
    }

    override fun onStart() {
        super.onStart()
        fireAuthentication = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        getUser()
    }

    override fun onStop() {
        super.onStop()
    }

    private fun getUser(){
        val fireUser = fireAuthentication.currentUser
        if (fireUser != null){
            var documentReference = firestore.collection("Users").document(fireUser.uid)
            documentReference.get().addOnSuccessListener{document ->
                if (document.getString("Email") != null && document.getString("FirstName") != null && document.getString("LastName") != null){
                    loggedUser = User(
                        document.getString("Email").toString(),
                        document.getString("FirstName").toString(),
                        document.getString("LastName").toString())
                    welcomeMessage.text = "Bonjour " + loggedUser.firstName
                }
            }
        }
        else
            onStop()
    }
}