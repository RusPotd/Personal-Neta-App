package com.example.basicotplogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.basicotplogin.AdapterClasses.BroadCastUserAdapter
import com.example.basicotplogin.AdapterClasses.UserAdapter
import com.example.basicotplogin.ModelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_create_broadcast.*

class create_broadcast : AppCompatActivity() {

    private var userAdapter: BroadCastUserAdapter? = null
    private var mUsers: List<Users>? = null
    private var recylerView: RecyclerView? = null
    private var broadCastName: String = "null"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_broadcast)

        val toolbar : Toolbar = findViewById(R.id.toolbar_broadcast_create)                 //create a back button on top of toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Create New BroadCasts"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            try{
                FirebaseDatabase.getInstance().reference.child("Broadcasts").child(broadCastName).removeValue()
                FirebaseDatabase.getInstance().reference.child("BroadCastDetails").child(broadCastName).removeValue()
            }
            catch(e: Exception){

            }
            val intent =  Intent(this@create_broadcast, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        var editText = findViewById<EditText>(R.id.enter_broadcast_name_txt)
        var editButton = findViewById<Button>(R.id.enter_broadcast_name_btn)
        editButton.setOnClickListener {
            broadCastName = editText.text.toString()
            if(broadCastName.trim().isNotEmpty()) {
                broadcast_name.setText(broadCastName)
                toolbar.title = ""
                editButton.visibility = View.GONE
                editText.visibility = View.GONE

                broadcast_name.visibility = View.VISIBLE
                post_broadcast_btn.visibility = View.VISIBLE
                hidden_layout.visibility = View.VISIBLE

                recylerView = findViewById(R.id.all_users)
                recylerView!!.setHasFixedSize(true)
                recylerView!!.layoutManager = LinearLayoutManager(applicationContext)

                mUsers = ArrayList()
                retrieveAllUsers(broadCastName)

                post_broadcast_btn.setOnClickListener {
                    var mapUsername = HashMap<String, Any>()
                    mapUsername["name"] = broadCastName
                    FirebaseDatabase.getInstance().reference.child("Broadcasts")
                        .child(broadCastName).updateChildren(mapUsername)
                    mapUsername = HashMap<String, Any>()
                    mapUsername["id"] = FirebaseAuth.getInstance().currentUser!!.uid
                    FirebaseDatabase.getInstance().reference.child("BroadCastDetails")
                        .child(broadCastName).child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .updateChildren(mapUsername)
                    val intent = Intent(this@create_broadcast, all_broadcast::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
            else{
                Toast.makeText(this@create_broadcast, "Broadcast name should not be empty!!!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun retrieveAllUsers(broadCastName : String) {
        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid

        val refUsers = FirebaseDatabase.getInstance().reference.child("Users")  //get all users ids

        refUsers.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()

                for (snapshot in p0.children){
                    val user: Users? = snapshot.getValue(Users::class.java)
                    if(!(user!!.getUID()).equals(firebaseUserID)){
                        (mUsers as ArrayList<Users>).add(user)
                    }

                }
                try {
                    userAdapter = BroadCastUserAdapter(this@create_broadcast, mUsers!!, false, broadCastName)
                    recylerView!!.adapter = userAdapter
                }
                catch (e: Exception) {

                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }
}