package com.exemplo.alertacoleta.global

import android.util.Log

object LogsDebug{
    private const val TAG = "DEBUG_APP"

    fun log(){
        // [0] é getStackTrace, [1] é esta função 'log', [2] é a função que chamou 'log'
        val caller = Thread.currentThread().stackTrace[3]
        val classeName = caller.className.substringAfterLast('.')
        val methodName = caller.methodName

        Log.i(TAG, "A função -> $methodName da classe -> $classeName foi chamada")
    }

    fun log(msg: String){
        // [0] é getStackTrace, [1] é esta função 'log', [2] é a função que chamou 'log'
        val caller = Thread.currentThread().stackTrace[3]
        val classeName = caller.className.substringAfterLast('.')
        val methodName = caller.methodName

        Log.i(TAG, "A função -> $methodName da classe -> $classeName foi chamada. Variavel: $msg")
    }
}