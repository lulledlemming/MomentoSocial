package com.dandy.momento.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.dandy.momento.R
import com.dandy.momento.models.Post
import com.dandy.momento.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter(private val mContext: Context,
                  private val mPost: List<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.post_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = mPost[position]

        Picasso.get().load(post.getPostImage()).into(holder.postImage)

        post.getPublisher()
            ?.let { publisherInfo(holder.profileImage, holder.userName, holder.publisher, it) }
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView){
        var profileImage: CircleImageView
        var postImage: ImageView
        var likeButton: ImageView
        var commentButton: ImageView
        var saveButton: ImageView
        var userName: TextView
        var likes: TextView
        var publisher: TextView
        var description: TextView
        var comments: TextView

        init {
            profileImage = itemView.findViewById(R.id.profileImagePost)
            postImage = itemView.findViewById(R.id.postImage_home)
            likeButton = itemView.findViewById(R.id.postLikeButton)
            commentButton = itemView.findViewById(R.id.postCommentButton)
            saveButton = itemView.findViewById(R.id.postSaveButton)
            userName = itemView.findViewById(R.id.postUserName)
            likes = itemView.findViewById(R.id.postLikes)
            publisher = itemView.findViewById(R.id.postPublisher)
            description = itemView.findViewById(R.id.postDescription)
            comments = itemView.findViewById(R.id.postComments)
        }
    }

    private fun publisherInfo(profileImage: CircleImageView, userName: TextView, publisher: TextView, publisherId: String){
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherId)

        usersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.ic_user_avatar_light).into(profileImage)
                    userName.text = user!!.getUsername()
                    publisher.text = user!!.getPublisher()
                }

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}