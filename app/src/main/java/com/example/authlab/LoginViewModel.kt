package com.example.authlab

import android.app.Application
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.crypto.Cipher

class LoginViewModel (application: Application) : AndroidViewModel(application){
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState : StateFlow<LoginUiState> = _uiState.asStateFlow()

    private var stored: EncryptedData? = null // in memory for now

    init {
        val saved = loadEncrypted(getApplication())
        if(saved != null){
            stored = saved
            val preview = android.util.Base64
                .encodeToString(saved.ciphertext, Base64.NO_WRAP).take(24)
            _uiState.value = LoginUiState.Locked(preview)
        }
    }


    fun onBiometricError(message: String){
        _uiState.value = LoginUiState.Failure(message)
    }


    fun onNoteLocked(unlockedCipher: Cipher, note: String) {
        val enc = encryptWith(unlockedCipher, note)
        stored = enc
        saveEncrypted(getApplication(),enc)
        val preview = Base64.encodeToString(enc.ciphertext, Base64.NO_WRAP).take(24)
        _uiState.value = LoginUiState.Locked(preview)
    }

    fun onNoteRevealed(unlockedCipher: Cipher) {
        val enc = stored ?: return
        val note= decryptWith(unlockedCipher, enc)
        _uiState.value = LoginUiState.Revealed(note )
    }

    fun storedData(): EncryptedData? = stored

    fun lockOnBackground(){
        // if a secret is currently revealed (or we hold one), snap back to Locked
        val enc = stored ?: return
        val preview = Base64.encodeToString(enc.ciphertext, Base64.NO_WRAP).take(24)
        _uiState.value = LoginUiState.Locked(preview)
    }
}