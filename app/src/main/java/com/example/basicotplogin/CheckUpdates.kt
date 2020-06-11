package com.example.basicotplogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class CheckUpdates : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}