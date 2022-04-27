package com.example.safebackhome.activities

import android.app.ActionBar
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.Layout
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginLeft
import androidx.recyclerview.widget.RecyclerView
import com.example.safebackhome.Data
import com.example.safebackhome.R
import com.example.safebackhome.adapters.ContactAdapter
import com.example.safebackhome.models.Contact
import com.example.safebackhome.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.Exception

class SettingsActivity : AppCompatActivity() {

    private lateinit var alertMessage: Button
    private lateinit var editPassword: Button
    private lateinit var editPin: Button
    private lateinit var editFakePin : Button
    private lateinit var addContact: Button
    private lateinit var logout : Button
    private lateinit var contactsRecyclerView : RecyclerView
    private lateinit var fireAuthentication : FirebaseAuth
    private lateinit var fireUser : FirebaseUser
    private lateinit var firestore : FirebaseFirestore
    private lateinit var loggedUser : User
    private lateinit var contactAdapter : ContactAdapter
    private val forbiddenPins = arrayOf("0000", "1234", "0123", "9876", "1111", "2222", "3333", "4444", "5555", "6666", "7777", "8888", "9999")
    private var realpin = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        try {
            declareViews()

            fireAuthentication = FirebaseAuth.getInstance()
            fireUser = fireAuthentication.currentUser!!
            loggedUser = Data.user
            firestore = FirebaseFirestore.getInstance()
            //alertMessage.setText("cc")

            addContact.setOnClickListener(object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    startActivity(Intent(applicationContext, AddContactActivity::class.java))
                }
            })

            logout.setOnClickListener(object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    fireAuthentication.signOut()
                    sendUserToNextActivity()
                }
            })

            editPassword.setOnClickListener(object : View.OnClickListener{
                override fun onClick(p0: View) {
                    editPassword(p0)
                }
            })

            editPin.setOnClickListener(object : View.OnClickListener{
                override fun onClick(p0: View) {
                    realpin = true
                    editPin(p0)
                }
            })

            editFakePin.setOnClickListener(object : View.OnClickListener{
                override fun onClick(p0: View) {
                    realpin = false
                    editPin(p0)
                }
            })

            alertMessage.setOnClickListener(object : View.OnClickListener{
                override fun onClick(p0: View) {
                    editAlert(p0)
                }
            })
        }
        catch (e:Exception){
            Data.logger(e)
        }
    }

    override fun onStart() {
        super.onStart()
        initiateRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        try{
            contactsRecyclerView.adapter?.notifyDataSetChanged()
        }
        catch (e : Exception){
            Log.e("Adapter Error", e.message.toString(), e)
        }

    }

    private fun changeImageButton(){

    }

    private fun declareViews(){
        editPassword = findViewById(R.id.editPassword_button)
        editPin = findViewById(R.id.editPin_button)
        editFakePin = findViewById(R.id.editFakePin_button)
        alertMessage = findViewById(R.id.editAlertMessage_button)
        logout = findViewById(R.id.logout_button)
        contactsRecyclerView = findViewById(R.id.settings_contactsList)
        addContact = findViewById(R.id.settings_addContact_button)
    }

    private fun initiateRecyclerView(){
        contactAdapter = ContactAdapter(getContacts())
        contactsRecyclerView.adapter = contactAdapter
    }

    private fun getFavorites() : ArrayList<Contact>{
        var list = ArrayList<Contact>()
        for (contact in Data.user.contacts){
            if (contact.isFavorite)
                list.add(contact)
        }
        return list
    }

    private fun getNonfavorites() : ArrayList<Contact>{
        var list = ArrayList<Contact>()
        for (contact in Data.user.contacts){
            if (!contact.isFavorite)
                list.add(contact)
        }
        return list
    }

    private fun getContacts() : ArrayList<Contact>{
        var list = ArrayList<Contact>()
        try {
            Data.user.contacts.forEach {
                if (it.isFavorite)
                    list.add(it)
            }
            Data.user.contacts.forEach {
                if (!it.isFavorite)
                    list.add(it)
            }
        }
        catch (e:Exception){
            Data.logger(e)
        }
        return list
    }

    public fun addFavorite(){

    }

    private fun sendUserToNextActivity() {
        var intent = Intent(applicationContext, LoginActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun editPassword(view: View){
        var resetPassword : EditText = EditText(view.context)
        resetPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        resetPassword.hint = "Nouveau mot de passe"
        var params = ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(45,0,45,0)
        resetPassword.layoutParams = params

        var layout = LinearLayout(view.context)
        layout.orientation = LinearLayout.VERTICAL
        layout.addView(resetPassword)

        val passwordResetDialog = AlertDialog.Builder(view.context)
        passwordResetDialog.setTitle("Changer de mot de passe ?")
        passwordResetDialog.setMessage("Entrez un nouveau mot de passe")
        passwordResetDialog.setView(layout)



        passwordResetDialog.setPositiveButton("Oui"){ dialog, which ->
            var newPassword = resetPassword.text.toString()
            fireUser.updatePassword(newPassword).addOnSuccessListener {
                Toast.makeText(this, "Mot de passe modifié", Toast.LENGTH_SHORT).show()
            }
                .addOnFailureListener { e->
                    Toast.makeText(this, "Echec du changement de mot de passe", Toast.LENGTH_SHORT).show()
                    Log.e("Password Error", e.message.toString(), e)
                }
        }

        passwordResetDialog.setNegativeButton("Non"){dialog, which -> }

        passwordResetDialog.create().show()
    }

    private fun editPin(view: View){
        var params = ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(65,0,45,0)
        params.width = 250


        var oldPinText = EditText(view.context)
        oldPinText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        oldPinText.hint = "actuel"
        oldPinText.layoutParams = params


        var newPinText = EditText(view.context)
        newPinText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        newPinText.layoutParams = params
        newPinText.hint = "nouveau"


        var layout = LinearLayout(view.context)
        layout.orientation = LinearLayout.VERTICAL
        layout.addView(oldPinText)
        layout.addView(newPinText)

        val pinResetDialog = AlertDialog.Builder(view.context)
        pinResetDialog.setTitle("Changer de PIN")
        pinResetDialog.setMessage("Entrez votre PIN actuel, puis entrez en un nouveau")
        pinResetDialog.setView(layout)

        pinResetDialog.setPositiveButton("Changer"){dialog, which ->
            var oldPin = oldPinText.text.toString()
            var newPin = newPinText.text.toString()
            var pin = loggedUser.pin
            var field = "PIN"
            if (!realpin) {
                field = "FakePin"
                pin = loggedUser.fakePin
            }
            if (oldPin.isEmpty())
                oldPinText.setError("Entrez votre pin actuel")
            else if (!oldPin.equals(pin))
                oldPinText.setError("Votre PIN ne correspond pas")
            else if (newPin.isEmpty())
                newPinText.setError("Veuillez entrer un nouveau code pin")
            else if (isForbidden(newPin) && realpin)
                newPinText.setError("ce code pin n'est pas autorisé")
            else if (newPin.length != 4)
                newPinText.setError("le code pin doit contenir 4 chiffres")
            else{
                try{
                    val userDocRef = firestore.collection("Users").document(Data.user.id)
                    userDocRef.update(field, newPin)
                        .addOnSuccessListener {
                            if (realpin)
                                loggedUser.pin = newPin
                            else loggedUser.fakePin = newPin
                            Log.d("Update User Debug: ", "upadte successful")
                            Toast.makeText(this, "Pin Modifié", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            e -> Log.e("Update User Error: ", e.message.toString(), e)
                            Toast.makeText(this, "Echec de la modification du pin", Toast.LENGTH_SHORT).show()
                        }
                }
                catch (e : Exception){
                    Log.e("Update User Error: ", e.message.toString(), e)
                }
            }
        }

        pinResetDialog.create().show()
    }

    private fun editAlert(view: View) {
        try{
            var params = ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT)
            params.setMargins(65,0,45,0)

            var alarmEditText = EditText(view.context)
            if (loggedUser.alertMessage != null)
                alarmEditText.setText(loggedUser.alertMessage)
            alarmEditText.layoutParams = params

            var editAlertMessageDialog = AlertDialog.Builder(view.context)
            editAlertMessageDialog.setTitle("Changer de message d'alerte")
            editAlertMessageDialog.setMessage("Entrez votre nouveau message d'alerte")
            editAlertMessageDialog.setView(alarmEditText)

            editAlertMessageDialog.setPositiveButton("Enregister"){dialog, which ->
                var newMessage = alarmEditText.text.toString()
                val userDocRef = firestore.collection("Users").document(Data.user.id)
                userDocRef.update("AlertMessage", newMessage)
                    .addOnSuccessListener {
                        loggedUser.alertMessage = newMessage
                        Log.d("Alert Message Debug", "Alert Message changed")
                        Toast.makeText(this, "Message d'alerte modifié", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                            e -> Log.e("Update User Error: ", e.message.toString(), e)
                        Toast.makeText(this, "Echec de la modification du message d'alerte", Toast.LENGTH_SHORT).show()
                    }
            }

            editAlertMessageDialog.setNegativeButton("Non"){dialog, which -> }

            editAlertMessageDialog.create().show()
        }
        catch (e:Exception){
            Data.logger(e)
        }
    }

    private fun isForbidden(pin : String) : Boolean{
        forbiddenPins.forEach {
            if (pin.equals(it))
                return true
        }

        return false
    }
}

