package com.example.safebackhome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    private lateinit var emailEdit : EditText
    private lateinit var passwordEdit : EditText
    private lateinit var submit : Button
    private lateinit var register : Button
    private lateinit var authentication : FirebaseAuth
    private lateinit var user : FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEdit = findViewById(R.id.login_email_edit)
        passwordEdit = findViewById(R.id.login_editpassword)
        submit = findViewById(R.id.login_submit_button)
        register = findViewById(R.id.login_register_button)
        authentication = FirebaseAuth.getInstance()
        //user = authentication.currentUser!!

        submit.setOnClickListener(object : View.OnClickListener{
            override fun onClick(view: View) {
                login()
            }
        })

        register.setOnClickListener(object : View.OnClickListener{
            override fun onClick(view: View) {
                startActivity(Intent(applicationContext, RegisterActivity::class.java))
            }
        })
    }

    private fun login() {
        var email = emailEdit.text.toString()
        var pass1 = passwordEdit.text.toString()

        if (email.isEmpty()){
            emailEdit.setError("Enter a correct email")
        }
        else if (pass1.isEmpty())
            passwordEdit.setError("enter a correct password")
        else
        {
            authentication.signInWithEmailAndPassword(email, pass1).addOnCompleteListener(this){task ->
                if (task.isSuccessful){
                    sendUserToNextActivity()
                    Toast.makeText(this, "login successful", Toast.LENGTH_SHORT).show()
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