package com.example.basicotplogin

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.basicotplogin.ModelClasses.Users
import com.google.android.gms.common.api.internal.TaskUtil
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.profile_image_settings

class SettingsActivity : AppCompatActivity() {

    var refUsers: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    var RequestCode = 438;
    var imageUri: Uri? = null
    var storageRef: StorageReference? = null
    var checkAdmin: Boolean = false
    var refAdmin: DatabaseReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        val toolbar : Toolbar = findViewById(R.id.toolbar_settings)                 //create a back button on top of toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Settings"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent =  Intent(this@SettingsActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        firebaseUser = FirebaseAuth.getInstance().currentUser

        //CHeck Admin
        refAdmin = FirebaseDatabase.getInstance().reference.child("Admin")

        refAdmin!!.addValueEventListener( object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.child("uid").value!!.equals(firebaseUser!!.uid)){
                    checkAdmin = true
                }

                if(checkAdmin){

                    refUsers = FirebaseDatabase.getInstance().reference.child("Admin")
                }
                else{
                    refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
                }

                storageRef = FirebaseStorage.getInstance().reference.child("User Images")

                refUsers!!.addValueEventListener( object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()) {
                            val user: Users? =
                                p0.getValue(Users::class.java)       //create user of instance Users class
                            enteredUsername.setText(user!!.getUsername())
                            enteredPhone.setText(user.getPhone())
                            enteredAddress.setText(user.getAddress())
                            Picasso.get().load(user.getProfile()).into(profile_image_settings)
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }

                })
            }

            override fun onCancelled(p0: DatabaseError) {}
        })

        save_btn.setOnClickListener {
            val mapUsername = HashMap<String, Any>()
            mapUsername["username"] = enteredUsername.text.toString()
            mapUsername["phone"] = enteredPhone.text.toString()
            mapUsername["address"] = enteredAddress.text.toString()
            mapUsername["search"] = enteredUsername.text.toString().toLowerCase()
            refUsers!!.updateChildren(mapUsername)
            Toast.makeText(this@SettingsActivity, "Changes Saved", Toast.LENGTH_LONG).show()

        }

        profile_image_settings.setOnClickListener {
            pickImage()
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
            Toast.makeText(this@SettingsActivity, "Uploading...", Toast.LENGTH_LONG).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(this@SettingsActivity)
        progressBar.setMessage("Image is uploading please wait...")
        progressBar.show()

        if(imageUri!=null){         //storing image in storage while avoiding multiple copies using time as unique constraint
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")

            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask (Continuation <UploadTask.TaskSnapshot, Task<Uri>>{task ->
                if(!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val mapProfileImg = HashMap<String, Any>()
                    mapProfileImg["profile"] = url
                    refUsers!!.updateChildren(mapProfileImg)

                    progressBar.dismiss()

                }
            }
        }

    }
}
