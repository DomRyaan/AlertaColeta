package com.exemplo.alertacoleta.presentationLayer.inicio

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.exemplo.alertacoleta.databinding.ActivityInitBinding
import com.google.android.material.tabs.TabLayoutMediator
import androidx.activity.viewModels
import com.exemplo.alertacoleta.presentationLayer.home.MainActivity
import com.exemplo.alertacoleta.global.MyApplication
import com.exemplo.alertacoleta.dataLayer.model.Repository

class InitAcitvity : AppCompatActivity() {
   private lateinit var binding: ActivityInitBinding
    private val locationViewModel: InitViewModel by viewModels()

    private val repository: Repository by lazy {
        (application as MyApplication).repository
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityInitBinding.inflate(layoutInflater)

        setContentView(binding.root)

        locationViewModel.locationResult.observe(this, repository)

        val viewPager = binding.fragmentViews
        val tabLayout = binding.tabs
        val buttonNext = binding.btnNext

        val adapter = FragmentSlideAdapters(this)

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager){
            tab, position ->

        }.attach()

        buttonNext.setOnClickListener {
            val ultimoIndice = adapter.itemCount - 1

            if (viewPager.currentItem == ultimoIndice){
                var intent = Intent(this@InitAcitvity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else {
                viewPager.currentItem += 1
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        repository.onCleared()
    }

}