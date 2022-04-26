package com.example.safebackhome.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.safebackhome.R

class PermissionsActivity : AppCompatActivity() {
    private lateinit var smsButton : Button
    private lateinit var callButton : Button
    private lateinit var coarceButton : Button
    private lateinit var fineButton : Button
    private lateinit var backButton : Button
    private lateinit var recognitionButton : Button
    private lateinit var continueButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions)

        declareViews()
        if (canContinue())
            startActivity(Intent(applicationContext, LoginActivity::class.java))

        smsButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                requestPermission("sms")
            }
        })
        callButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                requestPermission("call")
            }
        })
        coarceButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                requestPermission("coarce")
            }
        })
        fineButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                requestPermission("fine")
            }
        })
        backButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                requestPermission("back")
            }
        })
        recognitionButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                requestPermission("recognition")
            }
        })
        continueButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if (canContinue())
                    startActivity(Intent(applicationContext, LoginActivity::class.java))
            }
        })
    }

    private fun declareViews(){
        smsButton = findViewById(R.id.permissions_sms_button)
        callButton = findViewById(R.id.permissions_call_button)
        coarceButton = findViewById(R.id.permissions_coarse_button)
        fineButton = findViewById(R.id.permissions_fine_button)
        backButton = findViewById(R.id.permissions_back_button)
        recognitionButton = findViewById(R.id.permissions_recognition_button)
        continueButton = findViewById(R.id.permissions_continue_button)
    }

    private fun canContinue() : Boolean{
        var answer = true
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            answer = false
            smsButton.setError("Vous devez accepter cette autorisation")
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            answer = false
            callButton.setError("Vous devez accepter cette autorisation")
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            answer = false
            coarceButton.setError("Vous devez accepter cette autorisation")
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            answer = false
            fineButton.setError("Vous devez accepter cette autorisation")
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED){
            answer = false
            backButton.setError("Vous devez accepter cette autorisation")
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED){
            answer = false
            recognitionButton.setError("Vous devez accepter cette autorisation")
        }

        return answer
    }

    private fun requestPermission(permission: String){
        when(permission){
            "sms" ->{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 0)
            }
            "call" ->{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
            }
            "coarce" ->{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 2)
            }
            "fine" ->{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 3)
            }
            "back" ->{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), 4)
            }
            "recognition" ->{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 5)
            }
        }
    }
}