package com.example.basicotplogin.ModelClasses

class PostChatHist {
    private var  message : String = ""
    private var  sender : String = ""
    private var postId: String = ""


    constructor()

    constructor(message: String, sender: String, postId: String) {
        this.message = message
        this.sender = sender
        this.postId = postId
    }

    fun getMessage(): String?{
        return message
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun getSender(): String?{
        return sender
    }

    fun setSender(sender: String){
        this.sender = sender
    }

    fun getPostId(): String?{
        return postId
    }

    fun setPostId(postId: String){
        this.postId = postId
    }

}