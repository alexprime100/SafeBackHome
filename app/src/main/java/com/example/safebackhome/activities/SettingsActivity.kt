package com.example.safebackhome.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.safebackhome.Data
import com.example.safebackhome.R
import com.example.safebackhome.adapters.ContactAdapter
import com.example.safebackhome.models.Contact
import com.example.safebackhome.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

class SettingsActivity : AppCompatActivity() {

    private lateinit var alertMessage: Button
    private lateinit var editPassword: Button
    private lateinit var editPin: Button
    private lateinit var addContact: Button
    private lateinit var logout : Button
    private lateinit var contactsRecyclerView : RecyclerView
    private lateinit var fireAuthentication : FirebaseAuth
    private lateinit var fireUser : FirebaseUser
    private lateinit var firestore : FirebaseFirestore
    private lateinit var loggedUser : User
    private lateinit var contactAdapter : ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        declareViews()

        fireAuthentication = FirebaseAuth.getInstance()
        fireUser = fireAuthentication.currentUser!!
        firestore = FirebaseFirestore.getInstance()
        //alertMessage.setText("cc")


        /*saveButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val newPin = editPin.text.toString()
                if (!newPin.isEmpty()){
                    try {
                        val userDocRef = firestore.collection("Users").document(Data.user.id)
                        userDocRef.update("PIN", newPin)
                            .addOnSuccessListener { Log.d("Update User Debug: ", "upadte successful") }
                            .addOnFailureListener { e -> Log.e("Update User Error: ", e.message.toString(), e) }
                    }
                    catch (e :Exception){
                        Log.e("Update User Error: ", e.message.toString(), e)
                    }
                }
            }
        })*/

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
                EditPassword(p0)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        initiateRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        contactsRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun declareViews(){
        editPassword = findViewById(R.id.editPassword_button)
        editPin = findViewById(R.id.editPin_button)
        alertMessage = findViewById(R.id.editAlertMessage_button)
        logout = findViewById(R.id.logout_button)
        contactsRecyclerView = findViewById(R.id.settings_contactsList)
        addContact = findViewById(R.id.settings_addContact_button)
    }

    private fun initiateRecyclerView(){
        contactAdapter = ContactAdapter(getNonfavorites())
        contactsRecyclerView.adapter = contactAdapter
    }

    private fun getFavorites() : ArrayList<Contact>{
        var list = ArrayList<Contact>()
        for (contact in Data.user.contacts){
            if (contact.isFavortie)
                list.add(contact)
        }
        return list
    }

    private fun getNonfavorites() : ArrayList<Contact>{
        var list = ArrayList<Contact>()
        for (contact in Data.user.contacts){
            if (!contact.isFavortie)
                list.add(contact)
        }
        return list
    }

    private fun sendUserToNextActivity() {
        var intent = Intent(applicationContext, LoginActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun EditPassword(view: View){
        var resetPassword : EditText = EditText(view.context)
        resetPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        val passwordResetDialog = AlertDialog.Builder(view.context)
        passwordResetDialog.setTitle("Changer de mot de passe ?")
        passwordResetDialog.setMessage("Entrez un nouveau mot de passe")
        passwordResetDialog.setView(resetPassword)

        passwordResetDialog.setPositiveButton("Oui"){ dialog, which ->
            var newPassword = resetPassword.text.toString()
            fireUser.updatePassword(newPassword).addOnSuccessListener {
                Toast.makeText(this, "Mot de passe modifié", Toast.LENGTH_SHORT)
            }
                .addOnFailureListener { e->
                    Toast.makeText(this, "Echec du changement de mot de passe", Toast.LENGTH_SHORT)
                    Log.e("Password Error", e.message.toString(), e)
                }
        }

        passwordResetDialog.setNegativeButton("Non"){dialog, which ->

        }

        passwordResetDialog.create().show()
    }
}

