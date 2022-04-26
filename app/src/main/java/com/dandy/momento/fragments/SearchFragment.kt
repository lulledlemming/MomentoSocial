package com.dandy.momento.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dandy.momento.R
import com.dandy.momento.adapters.SearchAdapter
import com.dandy.momento.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {
    private var recyclerView: RecyclerView ? = null
    private var userAdapter: SearchAdapter ?= null
    private var mUser: MutableList<User> ?= null
    private var searchBox: EditText ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.searchRecyclerView)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        searchBox = view?.findViewById(R.id.searchBox)

        mUser = ArrayList()
        userAdapter = context?.let {SearchAdapter(it, mUser as ArrayList<User>, true)}
        recyclerView?.adapter = userAdapter

        searchBox?.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s:CharSequence?, start: Int, count: Int, after: Int){

            }

            override fun onTextChanged(s:CharSequence?, start: Int, before: Int, count: Int){
                if (searchBox!!.text.toString() == ""){

                } else {
                    recyclerView?.visibility = View.VISIBLE

                    retrieveUsers()
                    searchUser(s.toString().toLowerCase())
                }
            }

            override fun afterTextChanged(s: Editable?){

            }
        })

        return view
    }

    private fun retrieveUsers() {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users")
        searchBox = view?.findViewById(R.id.searchBox)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot){
                if (searchBox?.text.toString() == ""){
                    mUser?.clear()

                    for (snapshot in p0.children){
                        val user = snapshot.getValue(User::class.java)
                        if (user != null){
                            mUser?.add(user)
                        }
                    }

                    userAdapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun searchUser(input: String) {
        val userQuery = FirebaseDatabase.getInstance().getReference()
            .child("Users")
            .orderByChild("fullName")
            .startAt(input)
            .endAt(input + "\uf8ff")

        userQuery.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot){
                mUser?.clear()

                for (snapshot in p0.children){
                    val user = snapshot.getValue(User::class.java)
                    if (user != null){
                        mUser?.add(user)
                    }
                }

                userAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}