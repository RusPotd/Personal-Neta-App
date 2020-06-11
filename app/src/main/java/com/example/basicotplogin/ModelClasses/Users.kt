package com.example.basicotplogin.ModelClasses

class Users {
    private var  uid : String = ""
    private var  profile : String = ""
    private var  status : String = ""
    private var  search : String = ""
    private var  username : String = ""
    private var phone : String = ""
    private var address : String = ""



    constructor()

    constructor(uid: String, profile: String, status: String, search: String, username: String, phone: String, address: String) {
        this.uid = uid
        this.profile = profile
        this.status = status
        this.search = search
        this.username = username
        this.phone = phone
        this.address = address

    }

    fun getUID(): String?{
        return uid
    }

    fun setUID(uid: String){
        this.uid = uid
    }

    fun getUsername(): String?{
        return username
    }

    fun setUsername(username: String){
        this.username = username
    }

    fun getProfile(): String?{
        return profile
    }

    fun setProfile(profile: String){
        this.profile = profile
    }

    fun getStatus(): String?{
        return status
    }

    fun setStatus(status: String){
        this.status = status
    }

    fun getSearch(): String?{
        return search
    }

    fun setSearch(search: String){
        this.search = search
    }

    fun getPhone(): String?{
        return phone
    }

    fun setPhone(phone: String){
        this.phone = phone
    }

    fun getAddress(): String?{
        return address
    }

    fun setAddress(address: String){
        this.address = address
    }


}