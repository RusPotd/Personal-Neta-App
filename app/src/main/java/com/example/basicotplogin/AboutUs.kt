package com.example.basicotplogin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.widget.Toolbar

class AboutUs : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val progressBar = ProgressDialog(this@AboutUs)
        progressBar.setMessage("loading please wait...")
        progressBar.show()

        val toolbar : Toolbar = findViewById(R.id.toolbar_about)                 //create a back button on top of toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "About - Developer"


        val webView: WebView = findViewById(R.id.webView_about)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.dismiss()
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                url: String?
            ): Boolean {
                if(url!!.contains("about")) {
                    view!!.loadUrl(url);
                }
                else{
                    val intent =  Intent(this@AboutUs, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                return true
            }
        }

        webView.loadUrl("https://rushikeshpotdar.blogspot.com/p/about.html")
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}