package com.example.basicotplogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.basicotplogin.AdapterClasses.BroadCastUserAdapter
import com.example.basicotplogin.AdapterClasses.BroadcastListAdapter
import com.example.basicotplogin.ModelClasses.EditBroadCast
import com.example.basicotplogin.ModelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Edit_BroadCast : AppCompatActivity() {

    private var userAdapter: BroadcastListAdapter? = null
    private var mUsers: List<Users>? = null
    private var recylerView: RecyclerView? = null
    private var PreviousBroadcastList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit__broad_cast)

        var name = intent.getStringExtra("name")

        val toolbar : Toolbar = findViewById(R.id.toolbar_broadcast_edit)                 //create a back button on top of toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Edit BroadCast $name"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent =  Intent(this@Edit_BroadCast, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        recylerView = findViewById(R.id.all_users)
        recylerView!!.setHasFixedSize(true)
        recylerView!!.layoutManager = LinearLayoutManager(applicationContext)

        var refUsers = FirebaseDatabase.getInstance().reference.child("BroadCastDetails").child(name!!)
        refUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (snapShot in p0.children){
                    var broadCast: EditBroadCast? = snapShot.getValue(EditBroadCast::class.java)
                    PreviousBroadcastList.add(broadCast!!.getID().toString())
                }

                mUsers = ArrayList()
                retrieveAllUsers(name, PreviousBroadcastList)
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })




    }

    private fun retrieveAllUsers(broadCastName : String, PreviousBroadcastList: ArrayList<String>) {
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
                    userAdapter = BroadcastListAdapter(this@Edit_BroadCast, mUsers!!, false, broadCastName, PreviousBroadcastList)
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