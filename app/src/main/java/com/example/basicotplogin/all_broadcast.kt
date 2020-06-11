package com.example.basicotplogin

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.basicotplogin.ModelClasses.BroadCast
import com.google.firebase.database.*


class all_broadcast : AppCompatActivity() {

    var refBroadcast: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_broadcast)

        val toolbar : Toolbar = findViewById(R.id.toolbar_broadcast_all)                 //create a back button on top of toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "All BroadCasts"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent =  Intent(this@all_broadcast, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        var listView = findViewById<ListView>(R.id.view_broadcasts_list)

        var broadcastList: ArrayList<String> = ArrayList()
        var adapter: ArrayAdapter<String>

        refBroadcast = FirebaseDatabase.getInstance().reference.child("Broadcasts")

        refBroadcast!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                for (snapShot in p0.children){
                    var broadCast: BroadCast? = snapShot.getValue(BroadCast::class.java)
                    broadcastList.add(broadCast!!.getName().toString())

                }
                adapter = ArrayAdapter(this@all_broadcast, android.R.layout.simple_list_item_1, broadcastList)
                listView.adapter = adapter

                //ListElementsArrayList.add(GetValue.getText().toString());
                //            adapter.notifyDataSetChanged();
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        listView.setOnItemClickListener(OnItemClickListener { a, v, position, id ->
            val option = arrayOf<CharSequence>(
                "Edit Broadcast",
                "Delete Broadcast"
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@all_broadcast)
            builder.setTitle("What to do with Broadcast?")
            builder.setItems(option, DialogInterface.OnClickListener{ dialog, pos ->
                if(pos == 0){
                    val intent =  Intent(this@all_broadcast, Edit_BroadCast::class.java)
                    intent.putExtra("name", broadcastList[position])
                    startActivity(intent)
                }
                if(pos == 1){
                    refBroadcast!!.child(broadcastList[position]).removeValue()
                    broadcastList = ArrayList()
                }
            })
            builder.show()
        })



    }
}