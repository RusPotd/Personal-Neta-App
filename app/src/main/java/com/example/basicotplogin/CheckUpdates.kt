package com.example.basicotplogin

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class CheckUpdates : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update)

        val versionCode = BuildConfig.VERSION_CODE
        val versionName = BuildConfig.VERSION_NAME

        var refUpdate = FirebaseDatabase.getInstance().reference.child("Update")
        refUpdate.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(!p0.child("version").value!!.equals(versionName)){
                    //Toast.makeText(this@CheckUpdates, "Update available", Toast.LENGTH_LONG).show()
                    val option = arrayOf<CharSequence>(
                        "Update Now",
                        "Cancel"
                    )
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this@CheckUpdates)
                    builder.setTitle("Update available")
                    builder.setItems(option, DialogInterface.OnClickListener { dialog, position ->
                        if (position == 0) {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(p0.child("url").value.toString()))
                            startActivity(browserIntent)
                        }
                        if (position == 1) {
                            if(p0.child("cumpulsory").value!!.equals("false")){
                                val intent = Intent(this@CheckUpdates, MainActivity::class.java)
                                intent.putExtra("notUpdate", "true")
                                startActivity(intent)
                            }
                            else{
                                Toast.makeText(this@CheckUpdates, "App will won't run without update", Toast.LENGTH_LONG).show()
                                recreate()
                            }
                        }
                    })
                    try {
                        builder.show()
                    }
                    catch (e: Exception){
                        val intent = Intent(this@CheckUpdates, MainActivity::class.java)
                        startActivity(intent)
                    }

                }
                else
                {
                    Toast.makeText(this@CheckUpdates, "Already in letest version", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@CheckUpdates, MainActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}