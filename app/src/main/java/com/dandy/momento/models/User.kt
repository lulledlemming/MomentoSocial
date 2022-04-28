package com.dandy.momento.models

class User {
    private var bio: String ?= null
    private var fullName: String ?= null
    private var image: String ?= null
    private var uid: String ?= null
    private var userEmail: String ?= null
    private var userName: String ?= null

    constructor()

    constructor(
        bio: String?,
        fullName: String?,
        image: String?,
        uid: String?,
        userEmail: String?,
        userName: String?
    ) {
        this.bio = bio
        this.fullName = fullName
        this.image = image
        this.uid = uid
        this.userEmail = userEmail
        this.userName = userName
    }

    fun getBio(): String? {
        return bio
    }

    fun setBio(bio: String?){
        this.bio = bio
    }

    fun getFullName(): String? {
        return fullName
    }

    fun setFullName(fullName: String?){
        this.fullName = fullName
    }

    fun getImage(): String? {
        return image
    }

    fun setImage(image: String?){
        this.image = image
    }

    fun getUid(): String? {
        return uid
    }

    fun setUid(uid: String?){
        this.uid = uid
    }

    fun getUserEmail(): String? {
        return userEmail
    }

    fun setUserEmail(userEmail: String?){
        this.userEmail = userEmail
    }

    fun getUsername(): String? {
        return userName
    }

    fun setUsername(userName: String?){
        this.userName = userName
    }

}