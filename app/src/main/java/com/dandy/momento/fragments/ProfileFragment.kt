package com.dandy.momento.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.dandy.momento.ProfileSettings
import com.dandy.momento.R
import com.dandy.momento.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*


class ProfileFragment : Fragment() {
    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var editProfileButton: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null){
            this.profileId = pref.getString("profileId", "none").toString()
        }

        if (profileId == firebaseUser.uid){
            view.editProfile.text = "Edit Profile"

        } else if (profileId != firebaseUser.uid){
            checkWatchAndWatchingButtonStatus()

        }

        view.editProfile.setOnClickListener{
            val getButtonText = view.editProfile.text.toString()

            when {
                getButtonText == "Edit Profile" -> startActivity(Intent(context, ProfileSettings::class.java))

                getButtonText == "Watch" -> {
                    firebaseUser?.uid.let { itl ->
                        FirebaseDatabase.getInstance().reference
                            .child("Watch").child(itl.toString())
                            .child("Watching").child(profileId)
                            .setValue(true)
                    }

                    firebaseUser?.uid.let { itl ->
                        FirebaseDatabase.getInstance().reference
                            .child("Watch").child(profileId)
                            .child("Watchers").child(itl.toString())
                            .setValue(true)
                    }
                }

                getButtonText == "Watching" -> {
                    firebaseUser?.uid.let { itl ->
                        FirebaseDatabase.getInstance().reference
                            .child("Watch").child(itl.toString())
                            .child("Watching").child(profileId)
                            .removeValue()
                    }

                    firebaseUser?.uid.let { itl ->
                        FirebaseDatabase.getInstance().reference
                            .child("Watch").child(profileId)
                            .child("Watchers").child(itl.toString())
                            .removeValue()
                    }
                }

            }

        }

        getWatchers()
        getWatching()
        userInfo()

        return view
    }


    private fun checkWatchAndWatchingButtonStatus(){
        val watchingRef = firebaseUser.uid.let { itl ->
            FirebaseDatabase.getInstance().reference
                .child("Watch").child(itl.toString())
                .child("Watching")
        }

        if (watchingRef != null){
            watchingRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(p0: DataSnapshot){
                    if (p0.child(profileId).exists()){
                        view?.editProfile?.text = "Watching"
                    }else {
                        view?.editProfile?.text = "Watch"
                    }
                }

                override fun onCancelled(p0: DatabaseError){

                }
            })
        }
    }

    private fun getWatchers(){
        val watchersRef = FirebaseDatabase.getInstance().reference
            .child("Watch").child(profileId)
            .child("Watchers")

        watchersRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot){
                if (p0.exists()){
                    view?.watchCounter?.text = p0.childrenCount.toString()
                }
            }

            override fun onCancelled(p0: DatabaseError){

            }
        })
    }

    private fun getWatching(){
        val watchingRef = FirebaseDatabase.getInstance().reference
            .child("Watch").child(profileId)
            .child("Watching")

        watchingRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot){
                if (p0.exists()){
                    view?.watchingCounter?.text = p0.childrenCount.toString()
                }
            }

            override fun onCancelled(p0: DatabaseError){

            }
        })
    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)

        usersRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.ic_user_avatar_light).into(view?.profileImage)

                    view?.profileUsername?.text = user!!.getUsername()
                    view?.fullProfileName?.text = user!!.getFullName()
                    view?.profileBio?.text = user!!.getBio()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun onStop() {
        super.onStop()

        val pref = context?.getSharedPreferences("Prefs", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()

        val pref = context?.getSharedPreferences("Prefs", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()

        val pref = context?.getSharedPreferences("Prefs", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

}