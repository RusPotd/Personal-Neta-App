package com.example.basicotplogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.basicotplogin.ModelClasses.Users
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_view_info.*

class viewInfo : AppCompatActivity() {

    private var UID : String = ""
    private var refUser: DatabaseReference? = null
    private var username: TextView? = null
    private var phone: TextView? = null
    private var address: TextView? = null
    private var bio: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_info)

        val toolbar : Toolbar = findViewById(R.id.toolbar_view_info)                 //create a back button on top of toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent =  Intent(this@viewInfo, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        username = findViewById(R.id.profileName)
        phone = findViewById(R.id.profilePhone)
        address = findViewById(R.id.profileAddress)
        bio = findViewById(R.id.profileBio)

        UID = intent.getStringExtra("visit_uid")!!

        if(intent.hasExtra("admin")){
            refUser = FirebaseDatabase.getInstance().reference.child("Admin")
        }
        else{
            refUser = FirebaseDatabase.getInstance().reference.child("Users").child(UID)
        }

        refUser!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                toolbar.setTitle(p0.child("username").value.toString()+"'s Profile")
                username!!.setText(p0.child("username").value.toString())
                phone!!.setText(p0.child("phone").value.toString())
                address!!.setText(p0.child("address").value.toString())
                bio!!.setText(p0.child("bio").value.toString())
                Picasso.get().load(p0.child("profile").value.toString()).into(profileImage)
            }
        })



    }
}