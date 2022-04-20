package com.dandy.momento.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.dandy.momento.R
import com.dandy.momento.models.User

class SearchAdapter(private var mContext: Context,
                    private var mUser: List<User>,
                    private var isFragment: Boolean = false) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout, parent, false)
        return SearchAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchAdapter.ViewHolder, position: Int) {
        val user = mUser[position]
        holder.userNameTextView.text = user.getUsername()
        holder.userFullNameTextView.text = user.getUsername()
        holder.userNameTextView.text = user.getUsername()
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


}