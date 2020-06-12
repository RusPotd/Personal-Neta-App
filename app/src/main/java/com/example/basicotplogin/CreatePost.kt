package com.example.basicotplogin

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.basicotplogin.Fragments.APIService
import com.example.basicotplogin.ModelClasses.BroadCast
import com.example.basicotplogin.ModelClasses.EditBroadCast
import com.example.basicotplogin.ModelClasses.Posts
import com.example.basicotplogin.ModelClasses.Users
import com.example.basicotplogin.Notifications.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CreatePost : AppCompatActivity() {

    var refUsers: DatabaseReference? = null
    var refBroadCast: DatabaseReference? = null
    var refPosts: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    var RequestCode = 438;
    var imageUri: Uri? = null
    var storageRef: StorageReference? = null
    var url: String = "null"
    var postId: String? = null
    var post: Posts? = null
    var senderImage: String = ""
    var senderName: String = ""
    var apiService: APIService? = null
    var userIdVisit: String = "y33x7HMMKMRIIJn4XTI5YH1Xzv12" //"y33x7HMMKMRIIJn4XTI5YH1Xzv12"    //must change to every individual in users of perticular id
    var broadCastUsers: ArrayList<String> = ArrayList()
    var broadcastName : String = "public"
    var checkAdmin: Boolean = false
    var refAdmin: DatabaseReference? = null
    var time: Date? = null
    var refAllUsers: DatabaseReference? = null
    //var AllUsers: ArrayList<String>? = null
    var my_dict: HashMap<String, ArrayList<String>>? = null
    var gotList: Boolean = false
    var AdminUID: String = ""
    val mapUsername = HashMap<String, Any>()
    private var editData: String = ""
    private var editPostId: String = ""
    private var editImage: String = ""
    private var editGroup: String = ""
    private var EditPost: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        if(intent.hasExtra("dict")){
            my_dict = (intent.getSerializableExtra("dict") as HashMap<String, ArrayList<String>>)
            gotList = true
            Log.i("Archieved list ", "${my_dict.toString()}")
        }
        else{
            Log.i("No list ", "No list")
        }

        time = Calendar.getInstance().getTime()

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storageRef = FirebaseStorage.getInstance().reference.child("Posts Images")
        refPosts = FirebaseDatabase.getInstance().reference
        postId = refPosts!!.push().key

        //CHeck Admin and update spinner
        refAdmin = FirebaseDatabase.getInstance().reference.child("Admin")

        refAdmin!!.addValueEventListener( object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                AdminUID = p0.child("uid").value.toString()
                if (AdminUID.equals(firebaseUser!!.uid)) {
                    checkAdmin = true
                    //Toast.makeText(this@CreatePost, "Admin Found", Toast.LENGTH_SHORT).show()
                    refBroadCast = FirebaseDatabase.getInstance().reference.child("Broadcasts")
                }
                else{
                    refBroadCast = FirebaseDatabase.getInstance().reference.child("UserPostCategory")
                }

                refBroadCast!!.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        for (snapShot in p0.children) {
                            val user: BroadCast? = snapShot.getValue(BroadCast::class.java)
                            broadCastUsers.add(user!!.getName().toString())
                        }

                        val arrayAdapter = ArrayAdapter(
                            this@CreatePost,
                            android.R.layout.simple_spinner_dropdown_item,
                            broadCastUsers
                        )
                        spinner_group.adapter = arrayAdapter
                        spinner_group.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    //on nothing selected
                                    broadcastName = "public"
                                }

                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    broadcastName = broadCastUsers[position]
                                }
                            }

                        if(intent.hasExtra("id") && intent.hasExtra("data")){
                            EditPost = true
                            editData = intent.getStringExtra("data")!!
                            editPostId = intent.getStringExtra("id")!!
                            editImage = intent.getStringExtra("image")!!
                            editGroup = intent.getStringExtra("group")!!

                            postId = editPostId
                            if(!editImage.equals("null")){
                                url = editImage
                                post_image.visibility = VISIBLE
                                Picasso.get().load(editImage).into(post_image)
                            }
                            post_data.setText(editData)
                            spinner_group.setSelection(broadCastUsers.indexOf(editGroup))

                        }
                    }
                    override fun onCancelled(p0: DatabaseError) {}
                })
            }
            override fun onCancelled(p0: DatabaseError) {}
        })

        apiService = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)

        val toolbar : Toolbar = findViewById(R.id.toolbar_post_create)                 //create a back button on top of toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Create New Post"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            if(!EditPost) {
                FirebaseDatabase.getInstance().reference.child("Posts").child(firebaseUser!!.uid)
                    .child(postId!!).removeValue()
            }
            val intent =  Intent(this@CreatePost, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        //get User data
        refUsers!!.addValueEventListener( object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val user: Users? = p0.getValue(Users::class.java)       //create user of instance Users class

                    user_name_post.text = user!!.getUsername()
                    senderImage = user.getProfile().toString()
                    senderName = user.getUsername().toString()
                    Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile_image).into(profile_image_post)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

        post_image_btn.setOnClickListener {
            pickImage()
        }

        post_btn.setOnClickListener {
            sendPost()
        }

    }

    private fun sendPost() {

        mapUsername["data"] = post_data.text.toString()
        mapUsername["senderId"] = firebaseUser!!.uid
        mapUsername["group"] = broadcastName
        mapUsername["senderImage"] = senderImage
        mapUsername["senderName"] = senderName
        mapUsername["postId"] = postId!!
        mapUsername["time"] = time!!
        mapUsername["image"] = url


        refPosts!!.child("Posts").child(firebaseUser!!.uid).child(postId!!).updateChildren(mapUsername).addOnCompleteListener {
            Toast.makeText(this@CreatePost, "Post Published", Toast.LENGTH_LONG).show()
            /*if(checkAdmin){
                if(gotList){
                    var AllUsers = my_dict!![broadcastName]
                    Log.i("\n\n\nAllUsers : ", "$AllUsers\n\n\n")
                    for (MainItem in AllUsers!!){
                        for(Item in MainItem) {
                            sendNotification(Item.toString(), senderName, "Uploaded New Post")
                        }
                    }
                }
                else {
                    var AllUsers = my_dict!![broadcastName]
                    Log.i("\n\n\nAllUsers : ", "Not found\n\n\n")
                }
            }
            else{
                sendNotification(AdminUID, senderName, "Uploaded New Post")
            }*/
            val intent =  Intent(this@CreatePost, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RequestCode && resultCode == Activity.RESULT_OK && data!!.data != null){
            imageUri = data.data //pass image data to image uri variable
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(this@CreatePost)
        progressBar.setMessage("Getting Image, please wait...")
        progressBar.show()

        if(imageUri!=null){         //storing image in storage while avoiding multiple copies using time as unique constraint
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")

            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask (Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                if(!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val downloadUrl = task.result
                    url = downloadUrl.toString()

                    mapUsername["image"] = url
                    Picasso.get().load(url).into(post_image)
                    post_image.visibility = VISIBLE
                    try{
                        post!!.setImage(url)
                    }
                    catch(e: Exception){}
                    Toast.makeText(this@CreatePost, "Image Obtained", Toast.LENGTH_LONG).show()
                    progressBar.dismiss()

                }
            }
        }

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
                        "New_Post",
                        userIdVisit
                    )

                    val sender = Sender(data, token!!.getToken().toString())

                    apiService!!.sendNotification(sender)
                        .enqueue(object  : Callback<MyResponse> {
                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: retrofit2.Response<MyResponse>
                            ) {
                                if(response.code() == 200){
                                    if(response.body()!!.success != 1){
                                        Toast.makeText(this@CreatePost, "Failed, Nothing Happended", Toast.LENGTH_LONG).show()
                                    }
                                    else
                                    {
                                        //
                                    }
                                }
                                else{
                                    //
                                }
                            }

                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                                Toast.makeText(this@CreatePost, "Failed to push notification", Toast.LENGTH_LONG).show()
                            }
                        })
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    internal inner class CallNotify : AsyncTask<String, Void, String>(){

        override fun doInBackground(vararg AllUsers: String?): String {
            for (Item in AllUsers) {
                Log.i("Sending Notification", " to : $Item")

            }
            return ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }
    }
}