package com.example.safebackhome.activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.safebackhome.R
import com.example.safebackhome.models.Contact
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.HashMap

class RegisterActivity : AppCompatActivity() {

    private lateinit var emailEdit : EditText
    private lateinit var passwordEdit : EditText
    private lateinit var confirmPasswordEdit : EditText
    private lateinit var firstNameEdit : EditText
    private lateinit var lastNameEdit : EditText
    private lateinit var pinEdit : EditText
    private lateinit var fakePinEdit : EditText
    private lateinit var submit : Button
    private var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private lateinit var progressBar : ProgressBar
    private lateinit var fireAuthentication : FirebaseAuth
    private lateinit var fireUser : FirebaseUser
    private lateinit var fireStore: FirebaseFirestore
    private var forbiddenPins = arrayOf("0000", "1234", "0123", "9876", "1111", "2222", "3333", "4444", "5555", "6666", "7777", "8888", "9999")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        emailEdit = findViewById(R.id.register_email_edit)
        passwordEdit = findViewById(R.id.register_editpassword)
        confirmPasswordEdit = findViewById(R.id.register_editconfirmpassword)
        firstNameEdit = findViewById(R.id.register_editfirstname)
        lastNameEdit = findViewById(R.id.register_editlastname)
        pinEdit = findViewById(R.id.register_editPin)
        fakePinEdit = findViewById(R.id.register_editFakePin)

        submit = findViewById(R.id.register_submit_button)

        progressBar = ProgressBar(applicationContext)
        fireAuthentication = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()
        //user = authentication.currentUser!!

        submit.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?){
                authenticate()
            }
        })
    }

    fun authenticate(){
        var email = emailEdit.text.toString()
        var pass1 = passwordEdit.text.toString()
        var pass2 = confirmPasswordEdit.text.toString()
        var firstname = firstNameEdit.text.toString()
        var lastname = firstNameEdit.text.toString()
        var pin = pinEdit.text.toString()
        var fakePin = fakePinEdit.text.toString()

        if (!email.matches(Regex(emailPattern))){
            emailEdit.setError("Entrez une adresse email valide")
        }
        else if (pass1.isEmpty() || pass1.length < 5){
            passwordEdit.setError("entrez un mot de passe valide (au moins 5 caractères)")
        }
        else if (!pass1.equals(pass2)){
            confirmPasswordEdit.setError("les mots de passe doivent être identiques")
        }
        else if (pin.isEmpty() || pin.length != 4){
            pinEdit.setError("le code pin doit contenir 4 chiffres")
        }
        else if (isForbidden(pin)){
            pinEdit.setError("ce code pin n'est pas autorisé")
        }
        else if (fakePin.isEmpty() || fakePin.length != 4){
            fakePinEdit.setError("le code pin doit contenir 4 chiffres")
        }
        else{
            fireAuthentication.createUserWithEmailAndPassword(email, pass1).addOnCompleteListener(this){task ->
                if (task.isSuccessful){
                    fireUser = fireAuthentication.currentUser!!
                    var df = fireStore.collection("Users").document(fireUser.uid)
                    var contacts = ArrayList<Contact>()
                    contacts.add(Contact("papa", "123"))
                    contacts.add(Contact("maman", "321"))

                    var userInfo : HashMap<String, Any> = HashMap<String, Any>()
                    userInfo.put("FirstName", firstname)
                    userInfo.put("LastName", lastname)
                    userInfo.put("Email", email)
                    userInfo.put("PIN", pin)
                    userInfo.put("FakePin", fakePin)
                    df.set(userInfo)

                    contacts.forEach{
                        var df2 = fireStore.collection("Contacts").document()
                        var contactInfo : HashMap<String, Any> = HashMap<String, Any>()
                        contactInfo.put("UserId", fireUser.uid)
                        contactInfo.put("ContactFullName", it.fullName)
                        contactInfo.put("ContactNumber", it.phoneNumber)
                        df2.set(contactInfo)
                    }
                    sendUserToNextActivity()
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else{
                    Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun sendUserToNextActivity() {
        var intent = Intent(applicationContext, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun isForbidden(pin : String) : Boolean{
        forbiddenPins.forEach {
            if (pin.equals(it))
                return true
        }

        return false
    }
}