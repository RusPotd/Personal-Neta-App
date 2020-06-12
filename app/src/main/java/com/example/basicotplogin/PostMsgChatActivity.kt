package com.example.basicotplogin

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.basicotplogin.AdapterClasses.ChatHistAdapter
import com.example.basicotplogin.AdapterClasses.PostChatHistAdapter
import com.example.basicotplogin.Fragments.APIService
import com.example.basicotplogin.ModelClasses.ChatHist
import com.example.basicotplogin.ModelClasses.PostChatHist
import com.example.basicotplogin.ModelClasses.Users
import com.example.basicotplogin.Notifications.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.post_msg_chat_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostMsgChatActivity : AppCompatActivity() {

    var PostId: String = ""
    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null
    private var mUserChats: List<PostChatHist>? = null
    private var userAdapter: PostChatHistAdapter? = null
    private var recylerView: RecyclerView? = null
    var notify: Boolean = false
    var apiService: APIService? = null
    var refreshToken: String= ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_msg_chat_activity)

        val toolbar : Toolbar = findViewById(R.id.toolbar_messageChat_post)        //create a back button on top of toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent =  Intent(this@PostMsgChatActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        PostId = intent.getStringExtra("postId")!!

        firebaseUser = FirebaseAuth.getInstance().currentUser

        recylerView = findViewById(R.id.recycler_view_chats_post)
        recylerView!!.setHasFixedSize(false)
        recylerView!!.layoutManager = LinearLayoutManager(baseContext)

        mUserChats = ArrayList()
        retrieveAllChats()

        send_message_btn_post.setOnClickListener {
            notify = true
            val msg = text_message_post.text.toString()
            if(msg == ""){
                Toast.makeText(this@PostMsgChatActivity, "Please write a message first", Toast.LENGTH_LONG).show()  // !! indicates nor null asserted
            }
            else{
                sendMessageToUser(firebaseUser!!.uid, msg, PostId)
            }
            text_message_post.setText("")
        }
        //seenMessage(userIdVisit)
    }

    private fun retrieveAllChats() {
        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid

        val refUsers = FirebaseDatabase.getInstance().reference.child("PostChats").child(PostId)  //get all users ids

        refUsers.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mUserChats as ArrayList<PostChatHist>).clear()

                for (snapshot in p0.children){
                    val user: PostChatHist? = snapshot.getValue(PostChatHist::class.java)

                    (mUserChats as ArrayList<PostChatHist>).add(user!!)

                }
                try {
                    userAdapter = PostChatHistAdapter(baseContext!!, mUserChats!!, false)
                    recylerView!!.smoothScrollToPosition(userAdapter!!.itemCount);
                    recylerView!!.adapter = userAdapter
                }
                catch (e: Exception) {

                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }



    private fun sendMessageToUser(senderId: String, msg: String, postId: String) {
        val reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.push().key

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = msg
        messageHashMap["postId"] = postId

        reference.child("PostChats")
            .child(postId)
            .child(messageKey!!)
            .setValue(messageHashMap)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    //on task successfull
                }
            }

        //implement push notifications
        //Toast.makeText(this@PostMsgChatActivity, "pushing notification", Toast.LENGTH_SHORT).show()

    }

}
