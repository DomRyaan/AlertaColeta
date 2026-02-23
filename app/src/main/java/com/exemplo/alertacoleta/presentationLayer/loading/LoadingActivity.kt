package com.exemplo.alertacoleta.presentationLayer.loading

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.exemplo.alertacoleta.LogsDebug
import com.exemplo.alertacoleta.MyApplication
import com.exemplo.alertacoleta.R
import com.exemplo.alertacoleta.dataLayer.model.Repository
import com.exemplo.alertacoleta.presentationLayer.home.MainActivity
import com.exemplo.alertacoleta.presentationLayer.inicio.InitAcitvity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoadingActivity : AppCompatActivity() {
    private val repository: Repository by lazy {
        (application as MyApplication).repository
    }

    private var cidade: String? = null
    private var bairro: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loading)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        observarCidade()
        observarBairro()
    }

    override fun onStart() {
        super.onStart()

        lifecycleScope.launch {
            delay(900)
            if (cidade.isNullOrEmpty() || bairro.isNullOrEmpty()) {
                var intent = Intent(this@LoadingActivity, InitAcitvity::class.java)
                startActivity(intent)
            } else {
                var intent = Intent(this@LoadingActivity, MainActivity::class.java)
                startActivity(intent)
            }
            finish()
        }
    }

    private fun observarCidade(){
        lifecycleScope.launch {
            repository.dataStoreManager.cidadeFlow.collect { cidadeSalva ->
                if (cidadeSalva != null) {
                    cidade = cidadeSalva
                }
            }
        }
    }

    private fun observarBairro(){
        lifecycleScope.launch {
            repository.dataStoreManager.bairroFlow.collect { bairroSalvo ->
                if (bairroSalvo != null) {
                    bairro = bairroSalvo
                }

            }
        }
    }
}