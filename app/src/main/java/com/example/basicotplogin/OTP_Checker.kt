package com.example.basicotplogin

import android.content.ComponentCallbacks
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_o_t_p__checker.*
import java.util.concurrent.TimeUnit


class OTP_Checker : AppCompatActivity() {

    private var Username: String = ""
    private var Contact_no: String = ""
    private var Address: String = ""
    private var Bio: String = ""
    private var OTP: String = ""
    private var verificationId: String = ""
    lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var firebaseAuth: FirebaseAuth
    private var username_check: Boolean = false
    private var address_check: Boolean = false
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserID: String = ""
    private var gotBanned: Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_o_t_p__checker)

        firebaseAuth = FirebaseAuth.getInstance()


        username_check = intent.hasExtra("username")
        address_check = intent.hasExtra("address")

        if(username_check==true && address_check==true){
            Username = intent.getStringExtra("username")!!
            Contact_no = intent.getStringExtra("contact")!!
            Address = intent.getStringExtra("address")!!
            Bio = intent.getStringExtra("bio")!!
            Toast.makeText(this@OTP_Checker, "Obtained : "+Username+Contact_no+Address, Toast.LENGTH_LONG).show()
        }
        else{
            Contact_no = intent.getStringExtra("contact")!!
        }


        val toolbar : Toolbar = findViewById(R.id.toolbar_otp)                 //create a back button on top of toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Validate OTP"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent =  Intent(this@OTP_Checker, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }



        Send_OTP() // send otp

        verify_otp.setOnClickListener{
            SubmitOTP()
        }

        resend_otp.setOnClickListener {
            Send_OTP()
        }
    }

    private fun SubmitOTP() {
        OTP = entered_otp.text.toString()

        val credential = PhoneAuthProvider.getCredential(verificationId, OTP)

        signInWithPhoneAuthCredential(credential)

    }

    private fun verificationCallBacks(){
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {

            }

            override fun onCodeSent(
                verifiId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                verificationId = verifiId.toString()
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    getUserRegistered()
                    //val user = task.result?.user

                } else {

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(this@OTP_Checker, "Incorrect OTP", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun getUserRegistered() {
        var refBan = FirebaseDatabase.getInstance().reference.child("Banned")
        refBan.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                for (element in p0.children){
                    if(element.key.toString().equals(Contact_no.toString())){
                        gotBanned = true
                        Toast.makeText(this@OTP_Checker, "You have got banned from Admin, Please contact Admin", Toast.LENGTH_LONG).show()
                        FirebaseAuth.getInstance().signOut()
                        finishAndRemoveTask();
                    }
                }
            }
        })

        if(username_check==true && address_check==true && gotBanned==false) {

            firebaseUserID = firebaseAuth.currentUser!!.uid

            refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)


            val userHashMap = HashMap<String, Any>()
            userHashMap["uid"] = firebaseUserID
            userHashMap["profile"] = "gs://kaiseho-5e57c.appspot.com/profile_image.png"
            userHashMap["search"] = Username.toLowerCase()
            userHashMap["status"] = "offline"
            userHashMap["username"] = Username
            userHashMap["phone"] = Contact_no
            userHashMap["address"] = Address
            userHashMap["bio"] = Bio

            refUsers.updateChildren(userHashMap)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful)
                    {
                        if(Contact_no=="+917719811174"){
                            userHashMap["logged"] = "true"
                            FirebaseDatabase.getInstance().reference.child("Admin").updateChildren(userHashMap)
                        }
                        val intent =  Intent(this@OTP_Checker, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                }

            var refBroadCast = FirebaseDatabase.getInstance().reference.child("BroadCastDetails").child("public").child(firebaseUserID)
            val userHashMapBroadcast = HashMap<String, Any>()
            userHashMapBroadcast["id"] = firebaseUserID
            refBroadCast.updateChildren(userHashMapBroadcast)
        }
        else {
            if (gotBanned == false) {
                firebaseUserID = firebaseAuth.currentUser!!.uid

                refUsers =
                    FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)

                refUsers.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()) {
                            val intent = Intent(this@OTP_Checker, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@OTP_Checker,
                                "You are not registered!! Please Register",
                                Toast.LENGTH_LONG
                            ).show()
                            FirebaseAuth.getInstance().signOut()
                            val intent = Intent(this@OTP_Checker, RegisterActivity::class.java)
                            startActivity(intent)
                            finish()

                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {}
                })

            }
        }
    }

    private fun Send_OTP() {

        Toast.makeText(this@OTP_Checker, "OTP sent", Toast.LENGTH_LONG).show()

        verificationCallBacks()

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            Contact_no, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks) // OnVerificationStateChangedCallbacks

    }


}