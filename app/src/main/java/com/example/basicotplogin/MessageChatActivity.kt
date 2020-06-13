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
import com.example.basicotplogin.Fragments.APIService
import com.example.basicotplogin.ModelClasses.ChatHist
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
import kotlinx.android.synthetic.main.activity_message_chat.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessageChatActivity : AppCompatActivity() {

    var userIdVisit: String = ""
    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null
    private var mUserChats: List<ChatHist>? = null
    private var userAdapter: ChatHistAdapter? = null
    private var recylerView: RecyclerView? = null
    var notify: Boolean = false
    var apiService: APIService? = null
    var refreshToken: String= ""
    var phoneNumber: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        val toolbar : Toolbar = findViewById(R.id.toolbar_messageChat)        //create a back button on top of toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent =  Intent(this@MessageChatActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        apiService = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)


        try {
            //intent = intent
            userIdVisit = intent.getStringExtra("visit_id")!!
        }
        catch(e: Exception){
            onNewIntent(intent)
        }

        firebaseUser = FirebaseAuth.getInstance().currentUser

        reference = FirebaseDatabase.getInstance().reference          //code to get all details of user using userID
            .child("Users").child(userIdVisit)

        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val user: Users? = p0.getValue(Users::class.java)

                user_name_mc.text = user!!.getUsername()
                Picasso.get().load(user.getProfile()).into(profile_image_mc)
                phoneNumber = user.getPhone().toString()

                //retrieveMessages(firebaseUser!!.uid, userIdVisit, user.getProfile())

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


        call_profile.setOnClickListener {
            if(phoneNumber.length>2){
                var u: Uri = Uri.parse("tel:$phoneNumber");
                var i: Intent = Intent(Intent.ACTION_DIAL, u);
                startActivity(i)
            }
        }


        recylerView = findViewById(R.id.recycler_view_chats)
        recylerView!!.setHasFixedSize(false)
        recylerView!!.layoutManager = LinearLayoutManager(this@MessageChatActivity)

        mUserChats = ArrayList()
        retrieveAllChats()

        send_message_btn.setOnClickListener {
            notify = true
            val msg = text_message.text.toString()
            if(msg == ""){
                Toast.makeText(this@MessageChatActivity, "Please write a message first", Toast.LENGTH_LONG).show()  // !! indicates nor null asserted

            }
            else{
                reference = FirebaseDatabase.getInstance().reference
                var messageKey = reference!!.push().key
                sendMessageToUser(firebaseUser!!.uid, userIdVisit.toString(), msg, firebaseUser!!.uid, messageKey!!)
                sendMessageToUser(firebaseUser!!.uid, userIdVisit.toString(), msg, userIdVisit.toString(), messageKey)

            }
            text_message.setText("")
        }

        attach_image_file.setOnClickListener {
            notify = true
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent,"pick Image"), 438)
        }

        //seenMessage(userIdVisit)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val bundle: Bundle = intent!!.extras!!
        userIdVisit = bundle.getString("userid")!!
    }

    private fun retrieveAllChats() {
        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid

        val refUsers = FirebaseDatabase.getInstance().reference.child("Chats").child(firebaseUserID)  //get all users ids

        refUsers.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mUserChats as ArrayList<ChatHist>).clear()

                for (snapshot in p0.children){
                    val user: ChatHist? = snapshot.getValue(ChatHist::class.java)

                    if(user!!.getSender().equals(firebaseUserID) && user.getReceiver().equals(userIdVisit)
                        || user.getReceiver().equals(firebaseUserID) && user.getSender().equals(userIdVisit))
                    {
                        (mUserChats as ArrayList<ChatHist>).add(user!!)

                    }

                }
                try {
                    userAdapter = ChatHistAdapter(this@MessageChatActivity!!, mUserChats!!, false)
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

    private fun sendMessageToUser(senderId: String, receiverId: String, msg: String, CHILD: String, messageKey: String) {

        reference = FirebaseDatabase.getInstance().reference

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = msg
        messageHashMap["receiver"] = receiverId
        messageHashMap["url"] = ""
        messageHashMap["key"] = messageKey

        reference!!.child("Chats")
            .child(CHILD)
            .child(messageKey!!)
            .setValue(messageHashMap)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val chatsListReference = FirebaseDatabase.getInstance()
                        .reference
                        .child("ChatList")
                        .child(firebaseUser!!.uid)
                        .child(userIdVisit)
                    chatsListReference.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(p0: DataSnapshot) {
                            if(!p0.exists()){
                                chatsListReference.child("id").setValue(userIdVisit)

                            }

                            val chatsListReceiverReference = FirebaseDatabase.getInstance()
                                .reference
                                .child("ChatList")
                                .child(userIdVisit)
                                .child(firebaseUser!!.uid)
                            chatsListReceiverReference.child("id").setValue(firebaseUser!!.uid)

                        }

                        override fun onCancelled(p0: DatabaseError) {

                        }
                    })

                }
            }

        //implement push notifications
        Toast.makeText(this@MessageChatActivity, "pushing notification", Toast.LENGTH_SHORT).show()
        val ref = FirebaseDatabase.getInstance().reference
            .child("Users").child(firebaseUser!!.uid)

        ref.addValueEventListener( object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(Users::class.java)
                if(notify){
                    sendNotification(receiverId, user!!.getUsername(), msg)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    private fun sendNotification(receiverId: String, username: String?, msg: String) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val query = ref.orderByKey().equalTo(receiverId)

        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                for (dataSnapshot in p0.children)
                {
                    val token: Token? = dataSnapshot.getValue(Token::class.java)
                    val data = Data(
                        firebaseUser!!.uid,
                        R.mipmap.ic_launcher,
                        "$username: $msg",
                        "New Message",
                        userIdVisit
                    )

                    val sender = Sender(data, token!!.getToken().toString())

                    apiService!!.sendNotification(sender)
                        .enqueue(object  : Callback<MyResponse>{
                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {
                                if(response.code() == 200){
                                    if(response.body()!!.success != 1){
                                        Toast.makeText(this@MessageChatActivity, "Failed, Nothing Happended", Toast.LENGTH_LONG).show()
                                    }
                                    else
                                    {

                                    }
                                }
                                else{

                                }
                            }

                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                                Toast.makeText(this@MessageChatActivity, "Failed to push notification", Toast.LENGTH_LONG).show()
                            }
                        })
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==438 && resultCode==RESULT_OK && data!=null && data!!.data!=null){
            val progressBar = ProgressDialog(this)
            progressBar.setMessage("Image is uploading, please wait...")
            progressBar.show()

            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageReference.child("$messageId.jpg")

            var uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)

            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{  task ->

                if(!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }

                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
                if(task.isSuccessful){

                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val messageHashMap = HashMap<String, Any?>()   //save to database under Chats
                    messageHashMap["sender"] = firebaseUser!!.uid
                    messageHashMap["message"] = "sent you an image."
                    messageHashMap["receiver"] = userIdVisit
                    messageHashMap["key"] = messageId.toString()
                    messageHashMap["url"] = url

                    ref.child("Chats").child(firebaseUser!!.uid).child(messageId!!).setValue(messageHashMap)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                progressBar.dismiss()

                                val reference = FirebaseDatabase.getInstance().reference
                                    .child("Users").child(firebaseUser!!.uid)

                                reference.addValueEventListener( object : ValueEventListener{
                                    override fun onDataChange(p0: DataSnapshot) {
                                        val user = p0.getValue(Users::class.java)
                                        if(notify){
                                            sendNotification(userIdVisit, user!!.getUsername(), "sent you an image.")
                                        }
                                    }

                                    override fun onCancelled(p0: DatabaseError) {

                                    }
                                })
                            }
                        }
                    ref.child("Chats").child(userIdVisit).child(messageId!!).setValue(messageHashMap)


                }
            }
        }
    }

    /*private fun seenMessage(userId: String) {

        try {
            val reference = FirebaseDatabase.getInstance().reference.child("Chats")

            seenListner = reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    for (dataSnapshot in p0.children) {

                        val chat = dataSnapshot.getValue(Chat::class.java)

                        if (chat!!.getReceiver().equals(firebaseUser!!.uid) && chat!!.getSender()
                                .equals(userId)
                        ) {
                            val hashMap = HashMap<String, Any>()
                            hashMap["isseen"] = true
                            dataSnapshot.ref.updateChildren(hashMap)
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }

            })

        }
        catch(e: Exception)
        {
            Toast.makeText(this@MessageChatActivity, e.toString(), Toast.LENGTH_LONG).show()

        }

    }*/

}
