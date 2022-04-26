package com.example.safebackhome.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast

import com.example.safebackhome.R
import com.example.safebackhome.fragments.RegisterFragment1
import com.example.safebackhome.fragments.RegisterFragment2
import com.example.safebackhome.models.Contact
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

class Register2 : AppCompatActivity() {
    private lateinit var changeButton: Button
    private lateinit var submitButton : Button
    private var frag1 = true
    private var fragment1 = RegisterFragment1()
    private var fragment2 = RegisterFragment2()
    private lateinit var fireAuthentication : FirebaseAuth
    private lateinit var fireUser : FirebaseUser
    private lateinit var fireStore: FirebaseFirestore
    private var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private var forbiddenPins = arrayOf("0000", "1234", "0123", "9876", "1111", "2222", "3333", "4444", "5555", "6666", "7777", "8888", "9999")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_register2)
            supportFragmentManager.beginTransaction().replace(R.id.register_fragment, fragment1)
                .commit()

            changeButton = findViewById(R.id.fragment_button)
            submitButton = findViewById(R.id.register_submit_button)
            fireAuthentication = FirebaseAuth.getInstance()
            fireStore = FirebaseFirestore.getInstance()
        }
        catch (e:Exception){
            Log.e("Register Error", e.message.toString())
        }
        changeButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                var tr = supportFragmentManager.beginTransaction()
                if (frag1){
                    if (verification1()) {
                        frag1 = false
                        changeButton.text = "Précédent"
                        submitButton.visibility = View.VISIBLE
                        tr.replace(R.id.register_fragment, fragment2)
                    }
                }
                else{
                    frag1 = true
                    changeButton.text = "Continuer"
                    submitButton.visibility = View.INVISIBLE
                    tr.replace(R.id.register_fragment, fragment1)
                }
                tr.addToBackStack("TAG")
                tr.commit()
            }
        })

        submitButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                register()
            }
        })
    }

    private fun register(){
        try {
            if (verification2()) {
                var email = fragment1.emailText.text.toString()
                var pass1 = fragment1.passwordText.text.toString()
                var firstname = fragment2.fnameText.text.toString()
                var lastname = fragment2.lnameText.text.toString()
                var pin = fragment2.pinText.text.toString()
                var fakePin = fragment2.fakePinText.text.toString()

                fireAuthentication.createUserWithEmailAndPassword(email, pass1)
                    .addOnSuccessListener {
                        fireUser = fireAuthentication.currentUser!!
                        var id = fireUser.uid
                        var df = fireStore.collection("Users").document(id)
                        var contacts = ArrayList<Contact>()

                        contacts.add(Contact("", fireUser.uid, "papa", "123", false))
                        contacts.add(Contact("", fireUser.uid, "maman", "321", false))

                        var userInfo: HashMap<String, Any> = HashMap<String, Any>()
                        userInfo.put("Id", id)
                        userInfo.put("FirstName", firstname)
                        userInfo.put("LastName", lastname)
                        userInfo.put("Email", email)
                        userInfo.put("PIN", pin)
                        userInfo.put("FakePin", fakePin)
                        df.set(userInfo)

                        contacts.forEach {
                            var df2 = fireStore.collection("Contacts").document()
                            it.id = df2.id
                            var contactInfo: HashMap<String, Any> = HashMap<String, Any>()
                            contactInfo.put("UserId", fireUser.uid)
                            contactInfo.put("ContactFullName", it.fullName)
                            contactInfo.put("ContactNumber", it.phoneNumber)
                            df2.set(contactInfo)
                        }
                        sendUserToNextActivity()
                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Log.e("Register Error", e.message.toString(), e)
                    }
            }
        }
        catch (e:Exception){
            Log.e("Debug Error", e.message.toString(), e)
        }
    }

    private fun sendUserToNextActivity() {
        var intent = Intent(applicationContext, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun verification1() : Boolean{
        var email = fragment1.emailText.text.toString()
        var pass1 = fragment1.passwordText.text.toString()
        var pass2 = fragment1.passwordText2.text.toString()

        if (email.isEmpty())
            fragment1.emailText.setError("Veuillez entrer votre adresse courriel")
        else if (!email.matches(Regex(emailPattern)))
            fragment1.emailText.setError("entrez une adresse courriel valide")
        else if (pass1.isEmpty() || pass1.length < 6)
            fragment1.passwordText.setError("Veuillez entrer un mot de passe d'au moins 6 caractères")
        else if (pass2.isEmpty())
            fragment1.passwordText2.setError("Veuillez confirmer votre mot de passe")
        else if (!pass1.equals(pass2))
            fragment1.passwordText2.setError("les mots de passe doivent être identiques")
        else return true

        return false
    }

    private fun verification2() : Boolean{
        var fname = fragment2.fnameText.text.toString()
        var lname = fragment2.lnameText.text.toString()
        var pin = fragment2.pinText.text.toString()
        var fakePin = fragment2.fakePinText.text.toString()

        if (fname.isEmpty())
            fragment2.fnameText.setError("Veuillez entrer votre prénom")
        else if (lname.isEmpty())
             fragment2.lnameText.setError("Veuillez entrer votre nom")
        else if (pin.isEmpty() || pin.length != 4)
             fragment2.pinText.setError("Veuillez entrer un code pin de 4 chiffres")
        else if (fakePin.isEmpty() || fakePin.length != 4)
            fragment2.fakePinText.setError("Veuillez entrer un code pin de 4 chiffres")
        else if (isForbidden(pin))
            fragment2.pinText.setError("ce code pin n'est pas autorisé")
        else return true

        return false
    }

    private fun isForbidden(pin : String) : Boolean{
        forbiddenPins.forEach {
            if (pin.equals(it))
                return true
        }

        return false
    }
}