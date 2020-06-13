package com.example.basicotplogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.basicotplogin.AdapterClasses.PostAdapter
import com.example.basicotplogin.ModelClasses.EditBroadCast
import com.example.basicotplogin.ModelClasses.Posts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class MyPostsActivity : AppCompatActivity() {

    private var mUserChats: List<Posts>? = null
    private var userAdapter: PostAdapter? = null
    private var recylerView: RecyclerView? = null
    private var Admin: Boolean = false
    private var refUsers: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_posts_layout)

        mUserChats = ArrayList()

        recylerView = findViewById(R.id.recycler_view_post_myposts)
        recylerView!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        recylerView!!.layoutManager = linearLayoutManager

        val toolbar : Toolbar = findViewById(R.id.myposts_toolbar)                 //create a back button on top of toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "My Posts"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent =  Intent(this@MyPostsActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        refUsers = FirebaseDatabase.getInstance().reference.child("Posts").child(FirebaseAuth.getInstance().currentUser!!.uid)  //get all users ids

        refUsers!!.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (mUserChats as ArrayList<Posts>).clear()
                //Toast.makeText(context, "Retriving Admin Only", Toast.LENGTH_LONG).show()
                if(p0.exists()){
                    for (snapshot in p0.children) {
                        val user: Posts? = snapshot.getValue(Posts::class.java)

                        (mUserChats as ArrayList<Posts>).add(user!!)
                    }
                }
                try {
                    FirebaseDatabase.getInstance().reference.child("Admin")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(p0: DataSnapshot) {
                                if(p0.child("uid").value!!.equals(FirebaseAuth.getInstance().uid)){
                                    //Admin founded
                                    Admin = true
                                }
                                val MUserChats : MutableList<Posts> = ArrayList(mUserChats!!)
                                Collections.reverse(MUserChats)

                                userAdapter = PostAdapter(this@MyPostsActivity, MUserChats, Admin)
                                recylerView!!.isNestedScrollingEnabled = false
                                recylerView!!.adapter = userAdapter
                            }
                            override fun onCancelled(p0: DatabaseError) {
                                //on Cancel
                            }
                        })
                }
                catch (e: Exception) {

                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}