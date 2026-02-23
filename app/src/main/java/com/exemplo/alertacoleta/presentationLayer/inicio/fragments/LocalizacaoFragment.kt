package com.exemplo.alertacoleta.presentationLayer.inicio.fragments

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.exemplo.alertacoleta.dataLayer.model.LocalizacaoGPS
import com.exemplo.alertacoleta.databinding.FragmentLocalizacaoBinding
import com.exemplo.alertacoleta.presentationLayer.inicio.InitViewModel
import androidx.fragment.app.activityViewModels


class LocalizacaoFragment : Fragment() {
    private var _binding: FragmentLocalizacaoBinding? = null
    private val binding get() = _binding!!

   private val locationViewModel: InitViewModel by activityViewModels()
    private val localizacaoGPS by lazy { LocalizacaoGPS(requireActivity()) }

    private lateinit var editCidade: EditText
    private lateinit var editBairro: EditText
    private lateinit var close: ImageButton

    private val requestPermissaoLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
                    ) { isGranted: Boolean ->
                        if (isGranted) {
                                locationViewModel.fetchLocation(localizacaoGPS)
                        }
                    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =  FragmentLocalizacaoBinding.inflate( layoutInflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        editCidade = binding.editCidade
        editBairro = binding.editBairro
        close = binding.close

        binding.btnEnableLocation.setOnClickListener {
            if (localizacaoGPS.verificarPermissao()) {
                Toast.makeText(requireActivity(), "Permissão Concedida", Toast.LENGTH_SHORT)
                locationViewModel.fetchLocation(localizacaoGPSManager = localizacaoGPS)
            } else {
                requestPermissaoLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        binding.localManual.setOnClickListener {
            binding.cardFormulario.visibility = View.VISIBLE
        }

        binding.btnConfirmar.setOnClickListener {
            val resultado = locationViewModel.processarFormulario(editCidade, editBairro)

            Toast.makeText(requireActivity(), resultado, Toast.LENGTH_LONG)

            if ("sucesso" in resultado.lowercase()){
                fecharFormulario()
            }
        }

        binding.close.setOnClickListener {
            fecharFormulario()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun fecharFormulario(){
        binding.cardFormulario.visibility = View.GONE
    }
}