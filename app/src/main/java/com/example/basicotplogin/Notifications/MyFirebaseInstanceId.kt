package com.example.basicotplogin.Notifications

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseInstanceId: FirebaseMessagingService() {
    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val refreshToken = FirebaseInstanceId.getInstance().token

        if(firebaseUser!= null)
        {
            updateToken(refreshToken)
        }
    }

    private fun updateToken(refreshToken: String?)
    {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val token = Token(refreshToken!!)
        val ref = FirebaseDatabase.getInstance().getReference().child("Tokens").child(firebaseUser!!.uid).setValue(token)
    }
}