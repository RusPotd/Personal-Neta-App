package com.example.basicotplogin.Fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.basicotplogin.AdapterClasses.ChatUsersAdapter
import com.example.basicotplogin.CreatePost
import com.example.basicotplogin.ModelClasses.ChatUsers
import com.example.basicotplogin.ModelClasses.Users
import com.example.basicotplogin.R
import com.example.basicotplogin.SearchUser
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ChatsFragment : Fragment() {

    private var userAdapter: ChatUsersAdapter? = null
    private var mUsers: List<Users>? = null
    private var mChatUsers: ArrayList<String>? = null
    private var recylerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chats, container, false)

        view.findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener {
            val intent =  Intent(context, SearchUser::class.java)
            startActivity(intent)
        }
        //get all users that admin texted
        mChatUsers = ArrayList<String>()

        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid

        val refUsers = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUserID) //get all users ids
        refUsers.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                for (snapshot in p0.children){
                    val chatUser: ChatUsers? = snapshot.getValue(ChatUsers::class.java)

                    mChatUsers!!.add(chatUser!!.getID().toString())

                }

                try {
                    Log.d(TAG, "mChatUsers are: " + mChatUsers)
                }
                catch (e: Exception) {

                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        recylerView = view.findViewById(R.id.chatAllPeopleList)
        recylerView!!.setHasFixedSize(true)
        recylerView!!.layoutManager = LinearLayoutManager(context)

        mUsers = ArrayList()
        retrieveAllUsers()


        return view
    }

    private fun retrieveAllUsers() {
        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid

        val refUsers = FirebaseDatabase.getInstance().reference.child("Users")  //get all users ids

        refUsers.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()

                for (snapshot in p0.children){
                    val user: Users? = snapshot.getValue(Users::class.java)
                    if(!(user!!.getUID()).equals(firebaseUserID)){
                        if(mChatUsers!!.contains(user.getUID().toString())) {
                            (mUsers as ArrayList<Users>).add(user)
                        }
                    }

                }
                try {
                    userAdapter = ChatUsersAdapter(context!!, mUsers!!, false)
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

