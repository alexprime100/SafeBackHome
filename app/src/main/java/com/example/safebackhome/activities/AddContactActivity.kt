package com.example.safebackhome.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.example.safebackhome.Data
import com.example.safebackhome.R
import com.example.safebackhome.models.Contact
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.lang.Exception

class AddContactActivity : AppCompatActivity() {

    private lateinit var editName : EditText
    private lateinit var editPhone : EditText
    private lateinit var addButton : Button
    private lateinit var fireStore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        declareViews()

        fireStore = FirebaseFirestore.getInstance()

        addButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                var name = editName.text.toString()
                var phone = editPhone.text.toString()
                if (name.isEmpty()){
                    editName.setError("Veuillez entrer un nom")
                }
                else if (phone.isEmpty()){
                    editPhone.setError("Veuillez entrer un numéro de téléphone")
                }
                else {
                    try {
                        var df = fireStore.collection("Contacts").document()
                        var newContact = Contact(df.id, Data.user.id, name, phone)
                        df.set(newContact.toHashMap())
                        Data.user.contacts.add(newContact)
                    }
                    catch (e : Exception){

                    }
                    var intent = Intent(applicationContext, SettingsActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        })
    }

    private fun declareViews(){
        editName = findViewById(R.id.addContact_name_editText)
        editPhone = findViewById(R.id.addContact_phonenumber_editText)
        addButton = findViewById(R.id.addContact_add_button)
    }
}