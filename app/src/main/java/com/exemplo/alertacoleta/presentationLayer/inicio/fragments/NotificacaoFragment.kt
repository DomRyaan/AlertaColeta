package com.exemplo.alertacoleta.presentationLayer.inicio.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.exemplo.alertacoleta.dataLayer.model.Repository
import com.exemplo.alertacoleta.dataLayer.model.notification.NotificationHelper
import com.exemplo.alertacoleta.databinding.FragmentNotificationBinding
import com.exemplo.alertacoleta.global.MyApplication

class NotificacaoFragment : Fragment() {
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private val requestPermissaoLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGrated: Boolean ->
        if (isGrated){
            dispararNotificacaoTeste()
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

        _binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEnableNotification.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                dispararNotificacaoTeste()
            }else {
                requestPermissaoLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    fun dispararNotificacaoTeste(){
        val notification = NotificationHelper.NotificationBuilder("Teste", "Testando as notificações. Ignore essa notificação", requireContext())
        notification.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}