package com.example.basicotplogin

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar


class FeedBack : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feedback)

        val progressBar = ProgressDialog(this@FeedBack)
        progressBar.setMessage("loading please wait...")
        progressBar.show()

        val toolbar : Toolbar = findViewById(R.id.toolbar_feedback)                 //create a back button on top of toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "FeedBack - Developer"

        val webView: WebView = findViewById(R.id.webView_feedback)
        webView.settings.javaScriptEnabled = true
        webView.settings.allowContentAccess = true
        webView.settings.domStorageEnabled = true
        webView.settings.useWideViewPort = true
        webView.settings.setAppCacheEnabled(true)

        webView.webViewClient = object : WebViewClient () {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.dismiss()
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                url: String?
            ): Boolean {
                if(url!!.contains("contact-us-import-urlhttpsfonts")) {
                    view!!.loadUrl(url);
                }
                else{
                    val intent =  Intent(this@FeedBack, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                return true
            }
        }

        webView.loadUrl("https://rushikeshpotdar.blogspot.com/p/contact-us-import-urlhttpsfonts.html")


    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}