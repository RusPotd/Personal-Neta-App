package com.example.basicotplogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var Contact_no: String = "+91"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar : Toolbar = findViewById(R.id.toolbar_login)                 //create a back button on top of toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Login"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent =  Intent(this@LoginActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        login_otp_btn.setOnClickListener{

            Contact_no += contact_no_login.text

            if(Contact_no.isEmpty() || Contact_no.length < 13){
                contact_no_login.setError("Enter a valid mobile")
                contact_no_login.requestFocus()
            }
            else
            {
                val intent =  Intent(this@LoginActivity, OTP_Checker::class.java)
                intent.putExtra("contact", Contact_no)
                startActivity(intent)
                finish()
            }
        }
    }
}