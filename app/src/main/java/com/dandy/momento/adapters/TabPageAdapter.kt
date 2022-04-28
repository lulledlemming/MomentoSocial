package com.dandy.momento.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dandy.momento.fragments.*

class TabPageAdapter(activity: FragmentActivity, private val tabCount: Int) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = tabCount

    override fun createFragment(position: Int): Fragment 
    {
        return when (position){
            0 -> HomeFragment()
            1 -> SearchFragment()
            2 -> FeedFragment()
            3 -> ProfileFragment()
            else -> HomeFragment()
        }
    }
}