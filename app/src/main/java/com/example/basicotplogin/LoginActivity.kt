package com.example.basicotplogin

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
                var refUserAdmin = FirebaseDatabase.getInstance().reference.child("Admin")
                refUserAdmin.addListenerForSingleValueEvent( object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}

                    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.child("phone").value!!.equals(Contact_no)) {
                            Toast.makeText(this@LoginActivity, "Please Login Through Admin Login Panel!!!", Toast.LENGTH_LONG).show()
                            FirebaseAuth.getInstance().signOut()
                            val intent =  Intent(this@LoginActivity, WelcomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else{
                            val intent =  Intent(this@LoginActivity, OTP_Checker::class.java)
                            intent.putExtra("contact", Contact_no)
                            startActivity(intent)
                            finish()
                        }
                    }
                })
            }
        }
    }
}