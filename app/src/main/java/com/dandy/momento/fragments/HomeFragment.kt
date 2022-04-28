package com.dandy.momento.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dandy.momento.NewPostActivity
import com.dandy.momento.R
import com.dandy.momento.adapters.PostAdapter
import com.dandy.momento.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {
    private var postAdapter: PostAdapter ?= null
    private lateinit var postList: MutableList<Post>
    private lateinit var watchingList: MutableList<Post>
    private lateinit var newPostButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        var recyclerView: RecyclerView? = null
        recyclerView = view.findViewById(R.id.homeRecyclerView)
        newPostButton = view.findViewById(R.id.addPost)

        newPostButton.setOnClickListener{
            startActivity(Intent(context, NewPostActivity::class.java))
        }

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

        postList = ArrayList()
        postAdapter = context?.let {PostAdapter(it, postList as ArrayList<Post>)}
        recyclerView.adapter = postAdapter

        checkWatching()

        return view
    }

    private fun checkWatching(){
        watchingList = ArrayList()
        val watchingRef = FirebaseDatabase.getInstance().reference
            .child("Watch").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Watching")

        watchingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot){
                if (p0.exists()){
                    (watchingList as ArrayList<String>).clear()

                    for (snapshot in p0.children){
                        snapshot.key?.let { (watchingList as ArrayList<String>).add(it) }
                    }

                    retrievePosts()
                }
            }

            override fun onCancelled(p0: DatabaseError){

            }
        })

    }
    private fun retrievePosts() {
        postList = ArrayList()
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                postList.clear()

                for (snapshot in p0.children) {
                    val post = snapshot.getValue(Post::class.java)

                    for (id in (watchingList as ArrayList<String>)) {
                        if (post!!.getPublisher() == id.toString()) {
                            postList!!.add(post)
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}