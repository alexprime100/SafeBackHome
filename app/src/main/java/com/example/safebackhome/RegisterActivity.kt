package com.example.safebackhome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.HashMap

class RegisterActivity : AppCompatActivity() {

    private lateinit var emailEdit : EditText
    private lateinit var passwordEdit : EditText
    private lateinit var confirmPasswordEdit : EditText
    private lateinit var firstNameEdit : EditText
    private lateinit var lastNameEdit : EditText
    private lateinit var submit : Button
    private var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private lateinit var progressBar : ProgressBar
    private lateinit var fireAuthentication : FirebaseAuth
    private lateinit var fireUser : FirebaseUser
    private lateinit var fireStore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        emailEdit = findViewById(R.id.register_email_edit)
        passwordEdit = findViewById(R.id.register_editpassword)
        confirmPasswordEdit = findViewById(R.id.register_editconfirmpassword)
        firstNameEdit = findViewById(R.id.register_editfirstname)
        lastNameEdit = findViewById(R.id.register_editlastname)

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

        if (!email.matches(Regex(emailPattern))){
            emailEdit.setError("Enter correct email")
        }
        else if (pass1.isEmpty() || pass1.length < 5){
            passwordEdit.setError("Enter correct password")
        }
        else if (!pass1.equals(pass2)){
            confirmPasswordEdit.setError("both passwords must be identical")
        }
        else{
            fireAuthentication.createUserWithEmailAndPassword(email, pass1).addOnCompleteListener(this){task ->
                if (task.isSuccessful){
                    fireUser = fireAuthentication.currentUser!!
                    var df = fireStore.collection("Users").document(fireUser.uid)
                    var userInfo : HashMap<String, Any> = HashMap<String, Any>()
                    userInfo.put("FitstName", firstname)
                    userInfo.put("LastName", lastname)
                    userInfo.put("Email", email)
                    userInfo.put("Password", pass1)
                    df.set(userInfo)
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
}