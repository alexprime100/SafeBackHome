package com.example.safebackhome.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.safebackhome.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var emailEdit : EditText
    private lateinit var passwordEdit : EditText
    private lateinit var submit : Button
    private lateinit var reset: Button
    private lateinit var register : Button
    private lateinit var fireAuthentication : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var user : FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        declareViews()
        fireAuthentication = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        //user = authentication.currentUser!!

        submit.setOnClickListener(object : View.OnClickListener{
            override fun onClick(view: View) {
                login()
            }
        })

        register.setOnClickListener(object : View.OnClickListener{
            override fun onClick(view: View) {
                startActivity(Intent(applicationContext, Register2::class.java))
            }
        })

        reset.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View) {
                resetPassword(p0)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null){
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
    }

    private fun declareViews(){
        emailEdit = findViewById(R.id.login_email_edit)
        passwordEdit = findViewById(R.id.login_editpassword)
        submit = findViewById(R.id.login_submit_button)
        register = findViewById(R.id.login_register_button)
        reset = findViewById(R.id.login_forgottenpassword_button)
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
            fireAuthentication.signInWithEmailAndPassword(email, pass1).addOnCompleteListener(this){task ->
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

    private fun resetPassword(view: View){
        val resetMail = EditText(view.context)
        val passwordResetDialog = AlertDialog.Builder(view.context)
        passwordResetDialog.setTitle("Réinitialiser le mot de passe ?")
        passwordResetDialog.setMessage("Entrez votre courriel pour réinitialiser votre mot de passe")
        passwordResetDialog.setView(resetMail)

        passwordResetDialog.setPositiveButton("Oui"){ dialog, which ->
            var mail = resetMail.text.toString()
            fireAuthentication.sendPasswordResetEmail(mail).addOnSuccessListener {
                Toast.makeText(this, "Mot de passe modifié", Toast.LENGTH_SHORT).show()
            }
                .addOnFailureListener { e->
                    Toast.makeText(this, "Echec du changement de mot de passe", Toast.LENGTH_SHORT).show()
                    Log.e("Password Error", e.message.toString(), e)
                }
        }

        passwordResetDialog.setNegativeButton("Non"){dialog, which ->

        }

        passwordResetDialog.create().show()
    }

    private fun sendUserToNextActivity() {
        var intent = Intent(applicationContext, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}