package com.example.basicotplogin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_terms.*

class Terms : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)

        val progressBar = ProgressDialog(this@Terms)
        progressBar.setMessage("loading please wait...")
        progressBar.show()

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
                    if(intent.hasExtra("start")){
                        val intent =  Intent(this@Terms, WelcomeActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        val intent =  Intent(this@Terms, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                }
                return true
            }
        }

        webView.loadUrl("https://rushikeshpotdar.blogspot.com/p/terms-of-use.html")
    }

    override fun onBackPressed() {
        if(intent.hasExtra("start")){
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }
        else{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}