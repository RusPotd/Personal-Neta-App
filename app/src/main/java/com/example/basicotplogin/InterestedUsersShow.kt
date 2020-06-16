package com.example.basicotplogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.basicotplogin.AdapterClasses.UserAdapter
import com.example.basicotplogin.ModelClasses.EditBroadCast
import com.example.basicotplogin.ModelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class InterestedUsersShow : AppCompatActivity() {

    private var postId : String = ""
    private var time : String = ""
    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var recylerView: RecyclerView? = null
    private var searchEditText: EditText? = null
    private var mInterestedUsers: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interested_users_show)

        val toolbar : Toolbar = findViewById(R.id.toolbar_interested_users_show)                 //create a back button on top of toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent =  Intent(this@InterestedUsersShow, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        postId = intent.getStringExtra("id")!!
        time = intent.getStringExtra("time")!!

        toolbar.setTitle("$time Post Interested")

        recylerView = findViewById(R.id.searchList)
        recylerView!!.setHasFixedSize(true)
        recylerView!!.layoutManager = LinearLayoutManager(this@InterestedUsersShow)
        searchEditText = findViewById(R.id.searchUserET)

        var refInterest = FirebaseDatabase.getInstance().reference.child("Interested").child(postId)
        refInterest.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                for(snapShot in p0.children){
                    var user: EditBroadCast = snapShot.getValue(EditBroadCast::class.java)!!
                    mInterestedUsers.add(user.getID().toString())
                }
            }
        })

        mUsers = ArrayList()
        retrieveAllUsers()
    }

    private fun retrieveAllUsers() {
        val refUsers = FirebaseDatabase.getInstance().reference.child("Users")  //get all users ids

        refUsers.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()

                for (snapshot in p0.children){
                    val user: Users? = snapshot.getValue(Users::class.java)
                    if(mInterestedUsers.contains(user!!.getUID().toString())){
                        (mUsers as ArrayList<Users>).add(user)
                    }

                }
                try {
                    userAdapter = UserAdapter(this@InterestedUsersShow, mUsers!!, false, false)
                    recylerView!!.adapter = userAdapter
                }
                catch (e: Exception) {}

            }

            override fun onCancelled(p0: DatabaseError) {}
        })

    }
}