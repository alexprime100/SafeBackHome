package com.example.safebackhome.activites

import android.Manifest.permission
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.safebackhome.Data
import com.example.safebackhome.R
import com.example.safebackhome.models.Contact
import com.example.safebackhome.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var welcomeMessage : TextView
    private lateinit var fireAuthentication : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var loggedUser : User
    private lateinit var settings: ImageButton
    private lateinit var alertButton: Button
    private lateinit var contactsRecyclerView : RecyclerView
    private val MY_PERMISSIONS_REQUEST_SEND_SMS = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestSmsPermission()
        declareViews()

        //Listerers
        settings.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                startActivity(Intent(applicationContext, SettingsActivity::class.java))
            }
        })

        alertButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                sendSMS("test", "+33649550343")
            }
        })

    }

    private fun declareViews(){
        welcomeMessage = findViewById(R.id.name_textview)
        settings = findViewById(R.id.settings_button)
        alertButton = findViewById(R.id.alert_button)
    }

    override fun onStart() {
        super.onStart()
        fireAuthentication = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        getUser()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode){
            MY_PERMISSIONS_REQUEST_SEND_SMS -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "thanks for permitting", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestSmsPermission(){
        try {
            if (ContextCompat.checkSelfPermission(this, permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission.SEND_SMS)) { }
                else
                    ActivityCompat.requestPermissions(this, arrayOf(permission.SEND_SMS), MY_PERMISSIONS_REQUEST_SEND_SMS)
            }
        }
        catch (e :Exception){
            Log.e("SMS ERROR", e.message.toString())
        }
    }

    private fun getUser(){
        Log.d("User Debug", "test")
        val fireUser = fireAuthentication.currentUser
        if (fireUser != null){
            try{
                var contacts = ArrayList<Contact>()
                firestore.collection("Contacts").whereEqualTo("UserId", fireUser.uid).get()
                    .addOnSuccessListener { documents ->
                        for (document in documents){
                            Log.d("Contact Debug", "reading document")
                            if (document.getString("ContactFullName") != null &&
                                document.getString("ContactNumber") != null)
                            {
                                contacts.add(Contact(
                                    document.getString("UserId").toString(),
                                    document.getString("ContactFullName").toString(),
                                    document.getString("ContactNumber").toString()))
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("Contact Error", e.message.toString(), e)
                    }
                var documentReference = firestore.collection("Users").document(fireUser.uid)
                documentReference.get().addOnSuccessListener{document ->
                    if (document.getString("Email") != null &&
                        document.getString("FirstName") != null &&
                        document.getString("LastName") != null)
                    {

                        loggedUser = User(
                            document.getString("Id").toString(),
                            document.getString("Email").toString(),
                            document.getString("FirstName").toString(),
                            document.getString("LastName").toString(),
                            document.getString("Pin").toString(),
                            document.getString("fakePin").toString(),
                            contacts)
                        Log.d("User Debug", "nb contacts" + loggedUser.contacts.size)
                        welcomeMessage.text = "Bonjour " + loggedUser.firstName
                        Data.user = loggedUser
                    }
                }


            }
            catch (e : Exception){
                Log.e("User Error", e.message.toString())
                onStop()
            }
        }
        else
            onStop()
    }

    private fun sendSMS(message : String, number : String){
        try{
            SmsManager.getDefault().sendTextMessage(number, null, message, null, null)
            Toast.makeText(this, "sms sent", Toast.LENGTH_SHORT)
        }
        catch (e : Exception){
            Log.e("SMS ERROR", e.message.toString())
        }
    }
}