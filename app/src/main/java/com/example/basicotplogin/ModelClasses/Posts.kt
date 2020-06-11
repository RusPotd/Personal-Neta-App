package com.example.basicotplogin.ModelClasses

import java.util.*

class Posts {
    private var  data : String = ""
    private var  senderId : String = ""
    private var group : String = ""
    private var  image : String = ""
    private var senderImage: String = ""
    private var senderName: String = ""
    private var postId: String = ""
    private var time: Date? = null

    constructor()

    constructor(data: String, senderId: String, group: String, image: String, senderImage: String, senderName: String, postId: String) {
        this.data = data
        this.senderId = senderId
        this.group = group
        this.image = image
        this.senderImage = senderImage
        this.senderName = senderName
        this.postId = postId
        this.time = time
    }

    fun getData(): String?{
        return data
    }

    fun setData(data: String){
        this.data = data
    }

    fun getSenderId(): String?{
        return senderId
    }

    fun setSenderId(senderId: String){
        this.senderId = senderId
    }

    fun getGroup(): String?{
        return group
    }

    fun setGroup(group: String){
        this.group = group
    }

    fun getImage(): String?{
        return image
    }

    fun setImage(image: String){
        this.image = image
    }

    fun getSenderImage(): String?{
        return senderImage
    }

    fun setSenderImage(senderImage: String){
        this.senderImage = senderImage
    }

    fun getSenderName(): String?{
        return senderName
    }

    fun setSenderName(senderName: String){
        this.senderName = senderName
    }

    fun getPostId(): String?{
        return postId
    }

    fun setPostId(postId: String){
        this.postId = postId
    }

    fun getTime(): Date?{
        return time
    }

    fun setTime(time: Date){
        this.time = time
    }

}