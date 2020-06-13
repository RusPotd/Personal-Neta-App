package com.example.basicotplogin


import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        accept_terms_box.setOnClickListener {
            val intent =  Intent(this@WelcomeActivity, Terms::class.java)
            intent.putExtra("start", "start")
            startActivity(intent)
            finish()
        }

        Register_welcome_btn.setOnClickListener{
            if(accept_terms.isChecked()) {
                val intent = Intent(this@WelcomeActivity, RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(this@WelcomeActivity, "Please read and accept terms of use before processing", Toast.LENGTH_LONG).show()
            }
        }

        login_welcome_btn.setOnClickListener {
            if(accept_terms.isChecked()) {
                val intent =  Intent(this@WelcomeActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(this@WelcomeActivity, "Please read and accept terms of use before processing", Toast.LENGTH_LONG).show()
            }
        }

        adminLogin.setOnClickListener {
            if(accept_terms.isChecked()) {
                var refUserAdmin = FirebaseDatabase.getInstance().reference.child("Admin")
                refUserAdmin.addListenerForSingleValueEvent( object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}

                    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.child("logged").value!!.equals("true")) {
                            Toast.makeText(this@WelcomeActivity, "You are already Logged In from another Device", Toast.LENGTH_LONG).show()
                            FirebaseAuth.getInstance().signOut()
                            finishAndRemoveTask();
                        }
                        else
                        {
                            val userHashMap = HashMap<String, Any>()
                            userHashMap["logged"] = "true"
                            FirebaseDatabase.getInstance().reference.child("Admin").updateChildren(userHashMap).addOnCompleteListener {
                                Toast.makeText(applicationContext, "OTP sent to ******1174", Toast.LENGTH_LONG).show()
                                val Contact_no = "+917719811174"
                                val intent =  Intent(this@WelcomeActivity, OTP_Checker::class.java)
                                intent.putExtra("contact", Contact_no)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                })
            }
            else{
                Toast.makeText(this@WelcomeActivity, "Please read and accept terms of use before processing", Toast.LENGTH_LONG).show()
            }
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
