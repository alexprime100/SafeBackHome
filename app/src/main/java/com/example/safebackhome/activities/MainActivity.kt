package com.example.safebackhome.activities

import android.Manifest.permission
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import com.example.safebackhome.isPermissionGranted
import com.example.safebackhome.models.Contact
import com.example.safebackhome.models.User
import com.example.safebackhome.requestPermission
import com.example.safebackhome.service.DetectedActivityService
import com.example.safebackhome.service.requestActivityTransitionUpdates
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var welcomeMessage : TextView
    private lateinit var fireAuthentication : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var loggedUser : User
    private lateinit var settings: ImageButton
    private lateinit var alertButton: Button
    private lateinit var trajetButton: Button
    private lateinit var contactsRecyclerView : RecyclerView
    private val MY_PERMISSIONS_REQUEST_SEND_SMS = 0
    private val MY_PERMISSIONS_REQUEST_PHONE_CALL = 1
    private lateinit var callPoliceButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissions()
        declareViews()

        //Listeners
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
        callPoliceButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                callPolice()
            }
        })
        trajetButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                startActivity(Intent(applicationContext, MapActivity::class.java))
            }
        })

        if (isPermissionGranted()) {
            startService(Intent(this, DetectedActivityService::class.java))
            requestActivityTransitionUpdates()
            Toast.makeText(this@MainActivity, "You've started activity tracking",
                Toast.LENGTH_SHORT).show()
        } else {
            requestPermission()
        }
    }

    private fun declareViews(){
        welcomeMessage = findViewById(R.id.name_textview)
        settings = findViewById(R.id.settings_button)
        alertButton = findViewById(R.id.alert_button)
        callPoliceButton = findViewById(R.id.call_police_button)
        trajetButton = findViewById(R.id.trajet_button)
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
            MY_PERMISSIONS_REQUEST_PHONE_CALL -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "thanks for permitting", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestPermissions(){
        try {
            if (ContextCompat.checkSelfPermission(this, permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission.SEND_SMS)) { }
                else
                    ActivityCompat.requestPermissions(this, arrayOf(permission.SEND_SMS), MY_PERMISSIONS_REQUEST_SEND_SMS)
            }
            if (ContextCompat.checkSelfPermission(this, permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission.CALL_PHONE)) { }
                else
                    ActivityCompat.requestPermissions(this, arrayOf(permission.CALL_PHONE), MY_PERMISSIONS_REQUEST_PHONE_CALL)
            }
        }
        catch (e :Exception){
            Log.e("PERMISSION ERROR : ", e.message.toString(), e)
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
                            if (document.get("Id") != null &&
                                document.getString("ContactFullName") != null &&
                                document.getString("ContactNumber") != null)
                            {
                                contacts.add(Contact(
                                    document.getString("Id").toString(),
                                    document.getString("UserId").toString(),
                                    document.getString("ContactFullName").toString(),
                                    document.getString("ContactNumber").toString(),
                                    document.getBoolean("IsFavorite").toString().toBoolean()))
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
                            fireUser.uid,
                            document.getString("Email").toString(),
                            document.getString("FirstName").toString(),
                            document.getString("LastName").toString(),
                            document.getString("PIN").toString(),
                            document.getString("FakePin").toString(),
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

    private fun callPolice(){
        try{
            val callIntent : Intent = Uri.parse("tel:+33780037131").let { number ->
                Intent(Intent.ACTION_CALL, number)
            }
            val chooser = Intent.createChooser(callIntent, "Sélectionnez une application")
            startActivity(callIntent);
        }
        catch (e : ActivityNotFoundException){
            Log.e("Call Error: ", e.message.toString(), e)
        }
    }
}