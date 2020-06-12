package com.example.basicotplogin.ModelClasses

class ChatHist {
    private var  message : String = ""
    private var  receiver : String = ""
    private var  sender : String = ""
    private var  url : String = ""
    private var  key : String = ""

    constructor()

    constructor(message: String, receiver: String, sender: String, url: String, key: String) {
        this.message = message
        this.receiver = receiver
        this.sender = sender
        this.url = url
        this.key = key
    }

    fun getMessage(): String?{
        return message
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun getReceiver(): String?{
        return receiver
    }

    fun setReceiver(receiver: String){
        this.receiver = receiver
    }

    fun getSender(): String?{
        return sender
    }

    fun setSender(sender: String){
        this.sender = sender
    }

    fun getUrl(): String?{
        return url
    }

    fun setUrl(url: String){
        this.url = url
    }

    fun getKey(): String?{
        return key
    }

    fun setKey(key: String){
        this.key = key
    }

}