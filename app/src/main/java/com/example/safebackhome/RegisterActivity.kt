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

class RegisterActivity : AppCompatActivity() {

    private lateinit var emailEdit : EditText
    private lateinit var passwordEdit : EditText
    private lateinit var confirmPasswordEdit : EditText
    private lateinit var submit : Button
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private lateinit var progressBar : ProgressBar
    private lateinit var authentication : FirebaseAuth
    private lateinit var user : FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        emailEdit = findViewById(R.id.email_edit)
        passwordEdit = findViewById(R.id.editpassword)
        confirmPasswordEdit = findViewById(R.id.editconfirmpassword)
        submit = findViewById(R.id.submit_button)
        progressBar = ProgressBar(this)
        authentication = FirebaseAuth.getInstance()
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
            authentication.createUserWithEmailAndPassword(email, pass1).addOnCompleteListener(this){task ->
                if (task.isSuccessful){
                    sendUserToNextActivity()
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
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