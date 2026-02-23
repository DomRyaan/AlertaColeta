package com.exemplo.alertacoleta.presentationLayer.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exemplo.alertacoleta.MyApplication
import com.exemplo.alertacoleta.R
import com.exemplo.alertacoleta.dataLayer.dados.listaDeColetas
import com.exemplo.alertacoleta.dataLayer.model.Repository
import com.exemplo.alertacoleta.databinding.ActivityMainBinding
import com.exemplo.alertacoleta.presentationLayer.home.recycleColeta.ColetaAdpter

class MainActivity : AppCompatActivity() {
    private val repository: Repository by lazy {
        (application as MyApplication).repository
    }

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insents ->
            val systemBars = insents.getInsets(WindowInsetsCompat.Type.systemBars())

            v.updatePadding(
               left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right,
                bottom = systemBars.bottom
            )

            WindowInsetsCompat.CONSUMED
        }

        val meuAdapter = ColetaAdpter(listaDeColetas)

        val recyclerView = binding.calendarioColeta

        recyclerView.adapter = meuAdapter
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        recyclerView.layoutManager = layoutManager
    }
}