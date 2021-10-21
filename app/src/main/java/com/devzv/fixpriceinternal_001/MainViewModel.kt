package com.devzv.fixpriceinternal_001

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val mState = MutableStateFlow<State>(Default)
    val state get() = mState.asStateFlow()

    fun download(uri: Uri) {
        viewModelScope.launch {
            mState.value = Loading
            val res = try {
                val byteArray = downloadFile()
                saveFile(uri, byteArray)
                Complete("Done. Size: ${byteArray.size} bytes")
            } catch (e: Exception) {
                Error(e)
            }
            mState.value = res
        }
    }

    private suspend fun downloadFile(): ByteArray {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://sabnzbd.org/tests/internetspeed/50MB.bin")
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response.body!!.bytes()
            } else {
                throw Exception("Error code: ${response.code}")
            }
        }
    }

    private suspend fun saveFile(uri: Uri, byteArray: ByteArray) {
        withContext(Dispatchers.IO) {
            (getApplication() as Context).contentResolver.openOutputStream(uri)!!.use {
                it.write(byteArray)
            }
        }
    }

}

class MainViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(application) as T
    }
}