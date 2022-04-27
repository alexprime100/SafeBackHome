package com.example.safebackhome.activities

import android.Manifest
import android.Manifest.permission
import android.app.ActionBar
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.safebackhome.service.ServiceLocation
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
    private lateinit var callPoliceButton: Button
    private var alertMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fireAuthentication = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        getUser()
        //requestPermissions()
        /*permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            sendSMSGranted = permissions[Manifest.permission.SEND_SMS] ?: sendSMSGranted
            callPhoneGranted = permissions[Manifest.permission.SEND_SMS] ?: callPhoneGranted
            accessCoarseGranted = permissions[Manifest.permission.SEND_SMS] ?: accessCoarseGranted
            accessFineGranted = permissions[Manifest.permission.SEND_SMS] ?: accessFineGranted
            accessBackGranted = permissions[Manifest.permission.SEND_SMS] ?: accessBackGranted
            recognitionGranted = permissions[Manifest.permission.SEND_SMS] ?: recognitionGranted
        }
        requestPermission()*/
        declareViews()

        Log.d("EXITAPP", "mainActivity() has been called")


        //Listeners
        settings.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                startActivity(Intent(applicationContext, SettingsActivity::class.java))
            }
        })
        alertButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View) {
                if (alertMode)
                    disactivateAlert(p0)
                else activateAlert()
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

        /*if (isPermissionGranted()) {
            startService(Intent(this, DetectedActivityService::class.java))
            requestActivityTransitionUpdates()
            Toast.makeText(this@MainActivity, "You've started activity tracking",
                Toast.LENGTH_SHORT).show()
        } else {
            requestPermission()
        }
        Log.d("LOCATION_UPDATE", "startLocationService() has been called")
        requestPermission()
        startLocationService()*/
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
        if(!isLocationServiceRunning){
            startLocationService()
        }
    }

    private fun activateAlert(){
        try{
            if (loggedUser.alertMessage != null){
                startLocationService()
                loggedUser.getFavorites().forEach {
                    sendSMS(loggedUser.alertMessage, it.phoneNumber)
                }
            }
            else{
                Log.d("message debug", "message failed")
            }
            alertMode = true
        }
        catch (e : Exception){
            Data.logger(e)
        }
    }

    private fun disactivateAlert(view: View){
        var params = ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT)
        params.width = 250
        params.setMargins(65,0,45,0)

        var pinText = EditText(view.context)
        pinText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        pinText.hint = "PIN"
        pinText.layoutParams = params

        var pinEnterDialog = AlertDialog.Builder(view.context)
        pinEnterDialog.setTitle("Désactiver le mode alerte")
        pinEnterDialog.setMessage("Entrez votre code PIN")
        pinEnterDialog.setView(pinText)

        pinEnterDialog.setPositiveButton("Désactiver") { dialog, which ->
            var pin = pinText.text.toString()
            if (pin.isEmpty())
                pinText.setError("Entrez votre code pin")
            else if (pin.equals(loggedUser.pin)){
                stopLocationService()
                alertMode = false
                Log.d("Alert Debug : ", "alert mode disactivated")
                Toast.makeText(this, "Mode alerte désactivé", Toast.LENGTH_SHORT)
            }
            else if (pin.equals(loggedUser.fakePin)){
                Log.d("Alert Debug : ", "alert mode secretly not disactivated")
                Toast.makeText(this, "Mode alerte désactivé", Toast.LENGTH_SHORT)
            }
            else{
                pinText.setError("Votre code PIN est incorrect")
            }
        }
    }

    private fun getContacts() : ArrayList<Contact>{
        var contacts = ArrayList<Contact>()
        val fireUser = fireAuthentication.currentUser!!
        try{
            firestore.collection("Contacts").whereEqualTo("UserId", fireUser.uid).get()
                .addOnSuccessListener { documents ->
                    for (document in documents){
                        try{
                            var id = document.getString("Id").toString()
                            var name = document.getString("ContactFullName").toString()
                            var num = document.getString("ContactNumber").toString()
                            var uid = document.getString("UserId").toString()
                            var isF = document.getBoolean("IsFavortie")
                            var isF2 = false
                            if (isF != null)
                                isF2 = isF
                            var c = Contact(id, uid, name, num, isF2)
                            contacts.add(c)
                            Toast.makeText(this, c.fullName, Toast.LENGTH_SHORT)
                        }
                        catch (e:Exception ){
                            Data.logger(e)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Data.logger(e)
                }
        }
        catch (e:Exception){
            Data.logger(e)
        }
        return contacts
    }

    private fun getUser(){
        Log.d("User Debug", "test")
        val fireUser = fireAuthentication.currentUser!!
        var userId = fireUser.uid
        if (fireUser != null){
            try{
                var contacts = getContacts()
                /*firestore.collection("Contacts")/*.whereEqualTo("UserId", userId)*/.get()
                    .addOnSuccessListener { documents ->
                    var size = documents.size()
                    Toast.makeText(this, size, Toast.LENGTH_SHORT)
                    for (document in documents){

                        Log.d("Contact Debug", "reading document")
                        if (document.get("Id") != null &&
                            document.getString("ContactFullName") != null &&
                            document.getString("ContactNumber") != null &&
                            document.getString("UserId") != null)
                        {
                            if (document.getString("UserId").toString().equals(userId)){
                                contacts.add(Contact(
                                    document.getString("Id").toString(),
                                    document.getString("UserId").toString(),
                                    document.getString("ContactFullName").toString(),
                                    document.getString("ContactNumber").toString(),
                                    document.getBoolean("IsFavorite").toString().toBoolean()))
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Contact Error", e.message.toString(), e)
                }*/
                var documentReference = firestore.collection("Users").document(fireUser.uid)
                documentReference.get()
                    .addOnSuccessListener{document ->
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
                        if(document.getString("AlertMessage")!=null){
                            loggedUser.alertMessage = document.getString("AlertMessage").toString()
                        }
                        //Log.d("User Debug", "nb contacts" + loggedUser.contacts.size)
                        var str = loggedUser.toString()
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
            var geo = Geocoder(this)
            var adresses = geo.getFromLocation(ServiceLocation.latitude,ServiceLocation.longitude,1)
            lateinit var adress:Address
            lateinit var pos:String
            if(adresses.size>0){
                adress = adresses.get(0)
                pos = adress.getAddressLine(0)

            }
            else{
                pos = ""
            }
            Log.d("LOCATION_UPDATE", "Adresse : " + pos)
            SmsManager.getDefault().sendTextMessage(number, null, message+"Localisation : "+pos, null, null)
            Toast.makeText(this, "sms sent", Toast.LENGTH_SHORT).show()


        }
        catch (e : Exception){
            Log.e("SMS ERROR", e.message.toString())
        }
    }

    private fun callPolice(){
        try{
            val callIntent : Intent = Uri.parse("tel:911").let { number ->
                Intent(Intent.ACTION_DIAL, number)
            }
            val chooser = Intent.createChooser(callIntent, "Sélectionnez une application")
            startActivity(callIntent);
        }
        catch (e : ActivityNotFoundException){
            Log.e("Call Error: ", e.message.toString(), e)
        }
    }

    private val isLocationServiceRunning: Boolean
        private get() {
            val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            if (activityManager != null) {
                for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
                    if (ServiceLocation::class.java.name == service.service.className) {
                        if (service.foreground) {
                            return true
                        }
                    }
                }
                return false
            }
            return false
        }

    private fun startLocationService() {
        if (!isLocationServiceRunning) {
            val intent = Intent(applicationContext, ServiceLocation::class.java)
            intent.action = Data.ACTION_START_LOCATION_SERVICE
            startService(intent)
            Toast.makeText(this, "Location service started", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopLocationService() {
        if (isLocationServiceRunning) {
            val intent = Intent(applicationContext, ServiceLocation::class.java)
            intent.action = Data.ACTION_STOP_LOCATION_SERVICE
            startService(intent)
            Toast.makeText(this, "Location service stopped", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(isLocationServiceRunning){
            stopLocationService()
        }
    }

    companion object {
        private const val REQUEST_CODE_LOCATION_PERMISSION = 1
    }
}