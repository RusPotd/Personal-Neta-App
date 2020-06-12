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

class ViewAllComplaints : AppCompatActivity() {

    private var mUserChats: List<Posts>? = null
    private var userAdapter: PostAdapter? = null
    private var recylerView: RecyclerView? = null
    private var Admin: Boolean = true
    private var refUsers: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all_complaints)

        mUserChats = ArrayList()

        recylerView = findViewById(R.id.recycler_view_post_myposts)
        recylerView!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        recylerView!!.layoutManager = linearLayoutManager

        val toolbar : Toolbar = findViewById(R.id.myposts_toolbar)                 //create a back button on top of toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "All Complaints"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent =  Intent(this@ViewAllComplaints, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        refUsers = FirebaseDatabase.getInstance().reference.child("Posts") //.child(FirebaseAuth.getInstance().currentUser!!.uid)  //get all users ids

        refUsers!!.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (mUserChats as ArrayList<Posts>).clear()
                for (user in p0.children){
                    if(user.key!!.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                        continue
                    }
                    else{
                        for (postId in user.children){
                            val postData: Posts? = postId.getValue(Posts::class.java)
                            (mUserChats as ArrayList<Posts>).add(postData!!)
                        }
                    }
                }
                //Retriving all except Admin
                val MUserChats : MutableList<Posts> = ArrayList(mUserChats!!)
                Collections.sort(MUserChats, ComparatorClass())
                Collections.reverse(MUserChats)

                userAdapter = PostAdapter(applicationContext, MUserChats, Admin)
                recylerView!!.isNestedScrollingEnabled = false
                recylerView!!.adapter = userAdapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}