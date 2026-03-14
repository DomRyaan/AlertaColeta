package com.exemplo.alertacoleta.presentationLayer.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exemplo.alertacoleta.global.MyApplication
import com.exemplo.alertacoleta.R
import com.exemplo.alertacoleta.dataLayer.dados.DIAS_SEMANAS
import com.exemplo.alertacoleta.dataLayer.model.formatter.DataFormatter
import com.exemplo.alertacoleta.dataLayer.model.Repository
import com.exemplo.alertacoleta.databinding.ActivityMainBinding
import com.exemplo.alertacoleta.presentationLayer.home.recycleColeta.ColetaAdpter
import com.exemplo.alertacoleta.presentationLayer.home.viewmodel.MainViewModel
import com.exemplo.alertacoleta.presentationLayer.home.viewmodel.MainViewModelFactory

class MainActivity : AppCompatActivity() {
    private val repository: Repository by lazy {
        (application as MyApplication).repository
    }

    private lateinit var binding: ActivityMainBinding


    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(repository)
    }

    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestIgnoreBatteryOptimization(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.locationResult.observe(this, repository)

        val meuAdapter = ColetaAdpter(DIAS_SEMANAS, emptyList())

        recyclerView = binding.calendarioColeta
        recyclerView.adapter = meuAdapter
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        //    O código dentro do bloco só roda quando os dados chegarem.
        viewModel.listDiasTerao.observe(this) { diasComColeta ->
            diasComColeta?.let {
                meuAdapter.atualizarDiasComColeta(it)
                exibirCardInfo(it)
            }
        }

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

        viewModel.horario.observe(this) { horarioSalvo ->
            binding.horarioText.text = if (!horarioSalvo.isNullOrBlank()) "${DataFormatter.getHora(horarioSalvo)}:${DataFormatter.getMin(horarioSalvo)}"
                                        else ""
        }

        binding.iconAddLocalizacao.setOnClickListener {
            binding.formularioMain.root.visibility = View.VISIBLE
        }

        binding.formularioMain.close.setOnClickListener {
            binding.formularioMain.root.visibility = View.GONE
        }

        binding.formularioMain.btnConfirmar.setOnClickListener {
            val resultado = viewModel.processarFormulario(binding.formularioMain.editCidade, binding.formularioMain.editBairro)

            Toast.makeText(this, resultado, Toast.LENGTH_LONG).show()

            if ("sucesso" in resultado.lowercase()) {
                fecharFormulario()
            }
        }

        viewModel.localizacao.observe(this) { textoFormatado ->
            binding.localizacaoView.text = textoFormatado
        }
    }

    fun exibirCardInfo(listaColeta: List<String>){
        val haveraColeta = DataFormatter.temColetaHoje(listaColeta)

        if (haveraColeta) {
            binding.titleInfo.text = "Haverá Coleta"
            binding.textInformacoes.visibility = View.VISIBLE
        } else {
            binding.titleInfo.text = "Não haverá Coleta hoje"
        }
    }

    fun fecharFormulario(){
        binding.formularioMain.root.visibility = View.GONE
    }

    // Check and request battery optimization exemption
    fun requestIgnoreBatteryOptimization(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val packageName = context.packageName

        if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:$packageName")
            }
            context.startActivity(intent)
        }
    }
}
