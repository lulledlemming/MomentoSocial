package com.dandy.momento.models

class Post {

    private var postId: String ?= null
    private var postImage: String ?= null
    private var publisher: String ?= null
    private var description: String ?= null

    constructor()

    constructor (
        postId: String,
        postImage: String,
        publisher: String,
        description: String
    ) {
        this.postId = postId
        this.postImage = postImage
        this.publisher = publisher
        this.description = description
    }

    fun getPostId(): String? {
        return postId
    }

    fun getPostImage(): String? {
        return postImage
    }

    fun getPublisher(): String? {
        return publisher
    }

    fun getDescription(): String? {
        return description
    }

    fun setPostId(postId: String) {
        this.postId = postId
    }

    fun setPostImage(postImage: String) {
        this.postImage = postImage
    }

    fun setPublisher(publisher: String) {
        this.publisher = publisher
    }

    fun setDescription(description: String) {
        this.description = description
    }

}