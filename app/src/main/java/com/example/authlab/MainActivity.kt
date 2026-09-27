package com.example.authlab

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.authlab.crypto.BiometricStatus
import com.example.authlab.crypto.checkBiometricStatus
import com.example.authlab.crypto.getCryptoCipher
import com.example.authlab.crypto.getDecryptCipher
import com.example.authlab.crypto.showBiometricPrompt
import com.example.authlab.ui.LoginScreen
import com.example.authlab.ui.LoginViewModel
import com.example.authlab.ui.theme.AuthLabTheme

class MainActivity : FragmentActivity() {
    private val viewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AuthLabTheme {
                Scaffold(modifier = Modifier.fillMaxHeight()) { innerPadding ->
                    val state by viewModel.uiState.collectAsState()

                    LoginScreen(
                        state = state,
                        onLock = { note ->
                            when (checkBiometricStatus(this@MainActivity)) {
                                BiometricStatus.READY -> {
                                    val cipher = getCryptoCipher()  //cipher wired to the locked key
                                    showBiometricPrompt(
                                        activity = this@MainActivity,
                                        cipher = cipher,
                                        onSuccess = { unlockedCipher ->
                                            viewModel.onNoteLocked(unlockedCipher, note)

                                        },
                                        onError = { msg -> viewModel.onBiometricError(msg) }
                                    )
                                }

                                BiometricStatus.NOT_ENROLLED ->
                                    viewModel.onBiometricError("No Fingerprint enrolled. Add one in Settings.")

                                BiometricStatus.UNAVAILABLE ->
                                    viewModel.onBiometricError("Biometric unavailable on this device.")

                            }


                        },
                        onReveal = {
                            val enc = viewModel.storedData()
                            if (enc == null) {
                                viewModel.onBiometricError("Nothing to reveal yet.")
                            } else {
                                when (checkBiometricStatus(this@MainActivity)) {
                                    BiometricStatus.READY -> {
                                        val cipher = getDecryptCipher(enc.iv)      // decrypt cipher, with the saved IV
                                        showBiometricPrompt(
                                            activity = this@MainActivity,
                                            cipher = cipher,
                                            onSuccess = { unlockedCipher -> viewModel.onNoteRevealed(unlockedCipher) },
                                            onError = { msg -> viewModel.onBiometricError(msg) }
                                        )
                                    }

                                    BiometricStatus.NOT_ENROLLED ->
                                        viewModel.onBiometricError("No fingerprint enrolled. Add one in Settings.")

                                    BiometricStatus.UNAVAILABLE ->
                                        viewModel.onBiometricError("Biometrics not available on this device.")
                                }
                            }
                        },

                        modifier = Modifier.padding(innerPadding)
                            .padding(24.dp)
                    )



                }
            }
        }
    }

    override fun onPause(){
        super.onPause()
        viewModel.lockOnBackground()
    }
}


