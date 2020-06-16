package com.example.basicotplogin

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.basicotplogin.ModelClasses.BroadCast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_manage_user_group.*

class ManageUserGroup : AppCompatActivity() {

    var refBroadcast: DatabaseReference? = null
    var broadcastList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_user_group)

        val toolbar : Toolbar = findViewById(R.id.toolbar_user_group_all)                 //create a back button on top of toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Manage Complaint categories"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent =  Intent(this@ManageUserGroup, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        var listView = findViewById<ListView>(R.id.view_user_group_list)


        var adapter: ArrayAdapter<String>

        refBroadcast = FirebaseDatabase.getInstance().reference.child("UserPostCategory")

        refBroadcast!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                broadcastList = ArrayList()
                for (snapShot in p0.children){
                    var broadCast: BroadCast? = snapShot.getValue(BroadCast::class.java)
                    broadcastList.add(broadCast!!.getName().toString())
                }
                adapter = ArrayAdapter(this@ManageUserGroup, android.R.layout.simple_list_item_1, broadcastList)
                listView.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {}

        })

        listView.setOnItemLongClickListener(AdapterView.OnItemLongClickListener { a, v, position, id ->
            val option = arrayOf<CharSequence>(
                "Yes",
                "No"
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@ManageUserGroup)
            builder.setTitle("Delete Category?")
            builder.setItems(option, DialogInterface.OnClickListener { dialog, pos ->
                if (pos == 0) {
                    refBroadcast!!.child(broadcastList[position]).removeValue()
                    broadcastList = ArrayList()
                }
                if (pos == 1) {
                    this.recreate()
                }
            })
            builder.show()
            true
        })

        enter_user_group_btn.setOnClickListener {
            var broadCastName = enter_user_group_txt.text!!.toString()
            if(broadCastName.trim().isNotEmpty()) {
                var mapUsername = HashMap<String, Any>()
                mapUsername["name"] = broadCastName!!
                FirebaseDatabase.getInstance().reference.child("UserPostCategory")
                    .child(broadCastName).updateChildren(mapUsername)
                enter_user_group_txt.setText("")
            }
            else{
                Toast.makeText(this@ManageUserGroup, "Category Name must not be blank", Toast.LENGTH_LONG).show()
            }
        }
    }
}