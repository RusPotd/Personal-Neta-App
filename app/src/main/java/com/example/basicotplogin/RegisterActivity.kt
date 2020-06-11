package com.example.basicotplogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private var Username: String = ""
    private var Contact_no: String = "+91"
    private var Address: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val toolbar : Toolbar = findViewById(R.id.toolbar_register)                 //create a back button on top of toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent =  Intent(this@RegisterActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        Register_OTP_btn.setOnClickListener{
            getUserRegistered()
        }
    }

    private fun getUserRegistered() {
        Username = username_register.text.toString()
        Contact_no += contact_no_register.text.toString()
        Address = Address_register.text.toString()

        val intent =  Intent(this@RegisterActivity, OTP_Checker::class.java)
        intent.putExtra("username", Username)
        intent.putExtra("contact", Contact_no)
        intent.putExtra("address", Address)
        startActivity(intent)
        finish()
    }
}