package com.example.safebackhome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private lateinit var register: Button
    private lateinit var login: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        register = findViewById(R.id.register_button)
        login = findViewById(R.id.login_button)

        login.setOnClickListener(object : View.OnClickListener{
            override fun onClick(view: View?) {
                var intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            }
        })

        register.setOnClickListener(object : View.OnClickListener{
            override fun onClick(view: View) {
                var intent = Intent(applicationContext, RegisterActivity::class.java)
                startActivity(intent)
            }
        })
    }
}