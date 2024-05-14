package com.example.kessekolah.ui.onBoarding

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.kessekolah.R
import com.example.kessekolah.ui.onBoarding.content.OnBoardingContentFragment

class OnBoardingFragmentAdapter(
    fragmentActivity: FragmentActivity,
    private val context: Context
) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        val dataTitle = context.resources.getStringArray(R.array.boarding_title)
        val dataDescription = context.resources.getStringArray(R.array.boarding_description)
        return when (position) {
            0 -> OnBoardingContentFragment.newInstance(
                dataTitle[position],
                dataDescription[position],
                R.drawable.boarding_satu
            )
            1 -> OnBoardingContentFragment.newInstance(
                dataTitle[position],
                dataDescription[position],
                R.drawable.boarding_dua
            )
            2 -> OnBoardingContentFragment.newInstance(
                dataTitle[position],
                dataDescription[position],
                R.drawable.boarding_tiga
            )
            else -> OnBoardingContentFragment.newInstance(
                dataTitle[position],
                dataDescription[position],
                R.drawable.boarding_empat

            )
        }
    }

    override fun getItemCount(): Int {
        return 4
    }
}