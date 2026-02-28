package com.exemplo.alertacoleta.presentationLayer.home

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.exemplo.alertacoleta.global.MyApplication
import com.exemplo.alertacoleta.R
import com.exemplo.alertacoleta.dataLayer.dados.DIAS_SEMANAS
import com.exemplo.alertacoleta.dataLayer.model.formatter.DataFormatter
import com.exemplo.alertacoleta.dataLayer.model.Repository
import com.exemplo.alertacoleta.dataLayer.model.notification.NotificationWorker
import com.exemplo.alertacoleta.databinding.ActivityMainBinding
import com.exemplo.alertacoleta.global.LogsDebug
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
       // enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

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
            if (!horarioSalvo.isNullOrBlank()){
              binding.horarioText.text = "${DataFormatter.getHora(horarioSalvo)}:${DataFormatter.getMin(horarioSalvo)}"
            }
        }
    }

    fun exibirCardInfo(listaColeta: List<String>){
        val haveraColeta = DataFormatter.temColetaHoje(listaColeta)

        LogsDebug.log("Haverá coleta hoje: $haveraColeta")

        if (haveraColeta) {
            binding.titleInfo.text = "Haverá Coleta"
            binding.textInformacoes.visibility = View.VISIBLE
        } else {
            binding.titleInfo.text = "Não haverá Coleta hoje"
        }
    }
}
