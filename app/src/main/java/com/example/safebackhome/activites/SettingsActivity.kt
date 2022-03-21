package com.example.safebackhome.activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.safebackhome.Data
import com.example.safebackhome.R
import com.example.safebackhome.adapters.ContactAdapter
import com.example.safebackhome.models.Contact
import com.example.safebackhome.models.User
import com.example.safebackhome.viewHolders.ContactViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.log

class SettingsActivity : AppCompatActivity() {

    private lateinit var alertMessage: EditText
    private lateinit var editPassword: EditText
    private lateinit var editPin: EditText
    private lateinit var logout : Button
    private lateinit var contactsRecyclerView : RecyclerView
    private lateinit var fireAuthentication : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var loggedUser : User
    private lateinit var contactList : ArrayList<Contact>
    private lateinit var contactAdapter : ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        declareViews()

        fireAuthentication = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        //alertMessage.setText("cc")

        contactList = ArrayList<Contact>()
        contactList.add(Contact("papa", "123"))
        contactList.add(Contact("maman", "321"))


        logout.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                fireAuthentication.signOut()
                sendUserToNextActivity()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        initiateRecyclerView()
    }

    private fun declareViews(){
        editPassword= findViewById(R.id.editTextTextPassword)
        editPin= findViewById(R.id.editTextNumberPassword)
        alertMessage= findViewById(R.id.editAlertMessage)
        logout = findViewById(R.id.logout_button)
        contactsRecyclerView = findViewById(R.id.settings_contactsList)
    }

    private fun initiateRecyclerView(){
        contactAdapter = ContactAdapter(Data.user.contacts)
        contactsRecyclerView.adapter = contactAdapter
    }



    private fun sendUserToNextActivity() {
        var intent = Intent(applicationContext, LoginActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}

