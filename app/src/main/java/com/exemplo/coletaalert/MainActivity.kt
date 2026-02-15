package com.exemplo.coletaalert

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.exemplo.coletaalert.dataLayer.Localizacao
import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentContainerView
import com.exemplo.coletaalert.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    val LOCATION_PERMISSION_REQUEST_CODE = 100
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insents ->
            val systemBars = insents.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

            WindowInsetsCompat.CONSUMED
        }

        val localizacao = Localizacao(this)

        if (verificarPermissao()) {

            localizacao.obterLocalizacao { resultado ->
                runOnUiThread {
                    if (resultado.contains("não encontrada") || resultado.contains("Falha")){
                        binding.textView.text = "Não foi encontrado"
                    }
                    binding.textView.text = resultado
                }
            }

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    fun verificarPermissao(): Boolean {
        var permitidoFineLocation =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        var permitidoCoaseLocation =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (permitidoFineLocation == PackageManager.PERMISSION_GRANTED && permitidoCoaseLocation == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }
}