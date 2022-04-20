package com.dandy.momento.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.dandy.momento.R
import com.dandy.momento.fragments.ProfileFragment
import com.dandy.momento.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class SearchAdapter(private var mContext: Context,
                    private var mUser: List<User>,
                    private var isFragment: Boolean = false) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = mUser[position]
        holder.userNameTextView.text = user.getUsername()
        holder.userFullNameTextView.text = user.getFullName()
        Picasso.get().load(user.getImage())
            .placeholder(R.drawable.ic_user_avatar_light)
            .into(holder.userProfileImageView)

        user.getUid()?.let { checkWatchingStatus(it, holder.userWatchButton as Button) }

        holder.itemView.setOnClickListener{
            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("profileID", user.getUid())
            pref.apply()

            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.viewPager, ProfileFragment()).commit()
        }

        holder.userWatchButton.setOnClickListener{
            if(holder.userWatchButton.text.toString() == "Watch"){
                firebaseUser?.uid.let{ itl ->
                    user.getUid()?.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Watch").child(itl.toString())
                            .child("Watching").child(it1)
                            .setValue(true).addOnCompleteListener{ task ->
                                if (task.isSuccessful){
                                    firebaseUser?.uid.let{ itl ->
                                        FirebaseDatabase.getInstance().reference
                                            .child("Watch").child(user.getUid()!!)
                                            .child("Watchers").child(itl.toString())
                                            .setValue(true).addOnCompleteListener{ task ->
                                                if (task.isSuccessful){

                                                }
                                            }
                                    }
                                }
                            }
                    }
                }
            } else  {
                firebaseUser?.uid.let{ itl ->
                    user.getUid()?.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Watch").child(itl.toString())
                            .child("Watching").child(it1)
                            .removeValue().addOnCompleteListener{ task ->
                                if (task.isSuccessful){
                                    firebaseUser?.uid.let{ itl ->
                                        FirebaseDatabase.getInstance().reference
                                            .child("Watch").child(user.getUid()!!)
                                            .child("Watchers").child(itl.toString())
                                            .removeValue().addOnCompleteListener{ task ->
                                                if (task.isSuccessful){

                                                }
                                            }
                                    }
                                }
                            }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mUser.size
    }


    class ViewHolder(@NonNull itemView: View) :RecyclerView.ViewHolder(itemView) {
        var userNameTextView: TextView = itemView.findViewById(R.id.userName_search)
        var userFullNameTextView: TextView = itemView.findViewById(R.id.userFullName_search)
        var userProfileImageView: TextView = itemView.findViewById(R.id.userImage_search)
        var userWatchButton: TextView = itemView.findViewById(R.id.watchButton_search)


    }

    private fun checkWatchingStatus(uid: String, userWatchButton: Button){
        val watchingRef = firebaseUser?.uid.let{ itl ->
            FirebaseDatabase.getInstance().reference
                .child("Watch").child(itl.toString())
                .child("Watching")
        }

        watchingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(uid).exists()) {
                    userWatchButton.text = "Watching"
                } else {
                    userWatchButton.text = "Watch"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}