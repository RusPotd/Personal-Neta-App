package com.example.basicotplogin

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.basicotplogin.AdapterClasses.UserAdapter
import com.example.basicotplogin.ModelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_search_user.*

class SearchUser : AppCompatActivity() {

    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var recylerView: RecyclerView? = null
    private var searchEditText: EditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_user)

        val toolbar : Toolbar = findViewById(R.id.toolbar_search_user)                 //create a back button on top of toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Search Users"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent =  Intent(this@SearchUser, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        search_user_btn.setOnClickListener {
            searchUserET.visibility = View.VISIBLE
            toolbar_search_user.setBackgroundColor(Color.WHITE)
            toolbar.setTitle("")
        }

        recylerView = findViewById(R.id.searchList)
        recylerView!!.setHasFixedSize(true)
        recylerView!!.layoutManager = LinearLayoutManager(this@SearchUser)
        searchEditText = findViewById(R.id.searchUserET)

        mUsers = ArrayList()
        retrieveAllUsers()

        searchEditText!!.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(cs: CharSequence?, start: Int, before: Int, count: Int) {
                searchForUsers(cs.toString().trim().toLowerCase())
            }

        })
    }

    private fun retrieveAllUsers() {
        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid

        val refUsers = FirebaseDatabase.getInstance().reference.child("Users")  //get all users ids

        refUsers.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                if(searchEditText!!.text.toString() == ""){
                    for (snapshot in p0.children){
                        val user: Users? = snapshot.getValue(Users::class.java)
                        if(!(user!!.getUID()).equals(firebaseUserID)){
                            (mUsers as ArrayList<Users>).add(user)
                        }

                    }
                    try {
                        userAdapter = UserAdapter(this@SearchUser, mUsers!!, false, false)
                        recylerView!!.adapter = userAdapter
                    }
                    catch (e: Exception) {}
                }

            }

            override fun onCancelled(p0: DatabaseError) {}
        })

    }

    private fun searchForUsers(str: String){
        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid

        val queryUsers = FirebaseDatabase.getInstance().reference
            .child("Users").orderByChild("search")
            .startAt(str)
            .endAt(str + "\uf8ff")

        queryUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()

                for (snapshot in p0.children)
                {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    if(!(user!!.getUID()).equals(firebaseUserID))
                    {
                        (mUsers as ArrayList<Users>).add(user)
                    }

                }

                try {
                    userAdapter = UserAdapter(this@SearchUser, mUsers!!, false, false)
                    recylerView!!.adapter = userAdapter
                }
                catch(e: Exception){}

            }

            override fun onCancelled(p0: DatabaseError) {}
        })

    }
}