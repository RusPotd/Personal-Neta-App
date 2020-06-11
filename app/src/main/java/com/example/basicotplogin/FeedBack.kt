package com.example.basicotplogin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class FeedBack : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}