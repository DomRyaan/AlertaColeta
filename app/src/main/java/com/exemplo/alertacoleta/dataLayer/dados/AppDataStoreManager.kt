package com.exemplo.alertacoleta.dataLayer.dados

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.exemplo.alertacoleta.global.LogsDebug
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val USER_PREFERENCES_NAME = "user_preferences"


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

class AppDataStoreManager(private val context: Context) {
    // ---- FUNÇÕES DE ESCRITA (para salvar os dados) ----
    suspend fun salvarDadosLocalizacao(cidade: String, bairro: String){
        if (cidade.isNotEmpty() && bairro.isNotEmpty()) {
            LogsDebug.log("Salvando os dados: ${cidade}, ${bairro}")
            context.dataStore.edit { preferences ->
                preferences[CIDADE_KEY] = cidade
                preferences[BAIRRO_KEY] = bairro
            }
        }
    }

    suspend fun salvarDadosColeta(dias: String, horario: String) {
        LogsDebug.log("Os dados pego: ${dias}, ${horario}")
        if (dias.isNotEmpty() || horario.isNotEmpty()) {
            LogsDebug.log("Salvando os dados: ${dias}, ${horario}")
            context.dataStore.edit { preferences ->
                preferences[DIAS_COLETA_KEY] = dias
                preferences[HORARIO_COLETA_KEY] = horario
            }
        }
    }

    // ---- FUNÇÕES DE LEITURA (obter dados) ----
    val cidadeFlow: Flow<String?> = context.dataStore.data.map { preferences -> preferences[CIDADE_KEY] }
    val bairroFlow: Flow<String?> = context.dataStore.data.map { preferences -> preferences[BAIRRO_KEY] }
    val diasColetaFlow: Flow<String?> = context.dataStore.data.map { preferences -> preferences[DIAS_COLETA_KEY] }
    val horarioColetaFlow: Flow<String?> = context.dataStore.data.map { preferences -> preferences[HORARIO_COLETA_KEY] }


    companion object {
        val CIDADE_KEY = stringPreferencesKey("cidade_key")
        val BAIRRO_KEY = stringPreferencesKey("bairro_key")
        val DIAS_COLETA_KEY = stringPreferencesKey("dias_coleta_key")
        val HORARIO_COLETA_KEY = stringPreferencesKey("horario_coleta_key")


        @Volatile
        private var INSTANCE: AppDataStoreManager? = null

        fun getInstance(context: Context): AppDataStoreManager {
            return INSTANCE ?: synchronized(this) {
                val instance = AppDataStoreManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
