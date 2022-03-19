package com.example.safebackhome.activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.example.safebackhome.R
import com.example.safebackhome.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.log

class SettingsActivity : AppCompatActivity() {

    private lateinit var alertMessage: EditText
    private lateinit var editPassword: EditText
    private lateinit var editPin: EditText
    private lateinit var logout : Button
    private lateinit var fireAuthentication : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var loggedUser : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        editPassword= findViewById(R.id.editTextTextPassword)
        editPin= findViewById(R.id.editTextNumberPassword)
        alertMessage= findViewById(R.id.editAlertMessage)
        logout = findViewById(R.id.logout_button)

        fireAuthentication = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        //alertMessage.setText("cc")

        logout.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                fireAuthentication.signOut()
                sendUserToNextActivity()
            }
        })
    }

    override fun onStart() {
        super.onStart()
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
                }
            }
        }
        else
            onStop()
    }

    private fun sendUserToNextActivity() {
        var intent = Intent(applicationContext, LoginActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}

