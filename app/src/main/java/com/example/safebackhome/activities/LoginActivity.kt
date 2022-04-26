package com.example.safebackhome.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
    private val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /*if (Build.VERSION.SDK_INT >= 23) {
            checkMultiplePermissions();
        }
        val list = listOf<String>(
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )*/


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

    private fun checkMultiplePermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            var permissionsNeeded = ArrayList<String>()
            var permissionsList = ArrayList<String>()
            if (!addPermission(permissionsList, Manifest.permission.SEND_SMS)) {
                permissionsNeeded.add("SMS")
            }
            if (!addPermission(permissionsList, Manifest.permission.CALL_PHONE)) {
                permissionsNeeded.add("CALL")
            }
            if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                permissionsNeeded.add("COARSE")
            }
            if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION)) {
                permissionsNeeded.add("FINE")
            }
            if (!addPermission(permissionsList, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                permissionsNeeded.add("BACK")
            }
            if (!addPermission(permissionsList, Manifest.permission.ACTIVITY_RECOGNITION)) {
                permissionsNeeded.add("RECO")
            }

            if (permissionsList.size > 0) {
                requestPermissions(
                    permissionsList.toTypedArray(),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
                )
                return
            }
        }
    }

    private fun addPermission(permissionsList: ArrayList<String>, permission: String): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission)

                // Check for Rationale Option
                if (!shouldShowRequestPermissionRationale(permission)) return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
                val perms: MutableMap<String, Int> = HashMap()
                // Initial
                perms[Manifest.permission.SEND_SMS] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.CALL_PHONE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.ACCESS_COARSE_LOCATION] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.ACCESS_FINE_LOCATION] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.ACCESS_BACKGROUND_LOCATION] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.ACTIVITY_RECOGNITION] = PackageManager.PERMISSION_GRANTED

                // Fill with results
                var i = 0
                while (i < permissions.size) {
                    perms[permissions[i]] = grantResults[i]
                    i++
                }
                if (perms[Manifest.permission.ACCESS_FINE_LOCATION] == PackageManager.PERMISSION_GRANTED
                    && perms[Manifest.permission.SEND_SMS] == PackageManager.PERMISSION_GRANTED
                    && perms[Manifest.permission.CALL_PHONE] == PackageManager.PERMISSION_GRANTED
                    && perms[Manifest.permission.ACCESS_COARSE_LOCATION] == PackageManager.PERMISSION_GRANTED
                    && perms[Manifest.permission.ACCESS_BACKGROUND_LOCATION] == PackageManager.PERMISSION_GRANTED
                    && perms[Manifest.permission.ACTIVITY_RECOGNITION] == PackageManager.PERMISSION_GRANTED
                ) {
                    // All Permissions Granted
                    return
                } else {
                    // Permission Denied
                    if (Build.VERSION.SDK_INT >= 23) {
                        Toast.makeText(
                            applicationContext,
                            """
                            My App cannot run without Location and Storage Permissions.
                            Relaunch My App or allow permissions in Applications Settings
                            """.trimIndent(),
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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