package com.example.basicotplogin


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        Register_welcome_btn.setOnClickListener{
            val intent =  Intent(this@WelcomeActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        login_welcome_btn.setOnClickListener {
            val intent =  Intent(this@WelcomeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        adminLogin.setOnClickListener {
            Toast.makeText(applicationContext, "OTP sent to +917719811174", Toast.LENGTH_LONG).show()
            val Contact_no = "+917719811174"
            val intent =  Intent(this@WelcomeActivity, OTP_Checker::class.java)
            intent.putExtra("contact", Contact_no)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {   //if user already loged in skip process
        super.onStart()

        firebaseUser = FirebaseAuth.getInstance().currentUser;

        if(firebaseUser != null){
            val intent =  Intent(this@WelcomeActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
