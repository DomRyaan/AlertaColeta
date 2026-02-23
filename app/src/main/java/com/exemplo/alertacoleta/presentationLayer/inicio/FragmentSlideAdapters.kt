package com.exemplo.alertacoleta.presentationLayer.inicio

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.exemplo.alertacoleta.presentationLayer.inicio.fragments.InitFragment
import com.exemplo.alertacoleta.presentationLayer.inicio.fragments.LocalizacaoFragment
import com.exemplo.alertacoleta.presentationLayer.inicio.fragments.NotificacaoFragment

class FragmentSlideAdapters(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    private val paginas = listOf(
        InitFragment(),
        LocalizacaoFragment(),
        NotificacaoFragment()
    )

    override fun createFragment(position: Int): Fragment {
        return paginas[position]
    }

    override fun getItemCount(): Int {
        return paginas.size
    }
}