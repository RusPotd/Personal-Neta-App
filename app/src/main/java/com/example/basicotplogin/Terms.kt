package com.example.basicotplogin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Terms : AppCompatActivity() {

    private var temp: String =""
    private var firebaseUser: FirebaseUser? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)

        val progressBar = ProgressDialog(this@Terms)
        progressBar.setMessage("loading please wait...")
        progressBar.show()

        firebaseUser = FirebaseAuth.getInstance().currentUser

        val toolbar : Toolbar = findViewById(R.id.toolbar_terms)                 //create a back button on top of toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Terms of use"

        val webView: WebView = findViewById(R.id.webView_terms)
        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.dismiss()
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                url: String?
            ): Boolean {
                if(url!!.contains("terms-of-use")) {
                    view!!.loadUrl(url);
                }
                else{
                    val intent =  Intent(this@Terms, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                return true
            }
        }

        var refAdmin = FirebaseDatabase.getInstance().reference.child("Admin")

        refAdmin.addValueEventListener( object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child("uid").value!!.equals(firebaseUser!!.uid)) {
                    temp = "https://rushikeshpotdar.blogspot.com/p/terms-of-use.html"
                } else {
                    temp = "https://rushikeshpotdar.blogspot.com/p/terms-of-use_13.html"
                }
                webView.loadUrl(temp)
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}