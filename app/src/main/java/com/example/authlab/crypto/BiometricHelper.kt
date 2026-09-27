package com.example.authlab.crypto

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import javax.crypto.Cipher

enum class BiometricStatus {
    READY,          // hardware present, fingerprint enrolled — good to go
    NOT_ENROLLED,   // hardware present, but no fingerprint set up yet
    UNAVAILABLE     // no hardware, or otherwise can't be used
}

fun checkBiometricStatus(context: Context): BiometricStatus {
    val manager = BiometricManager.from(context)
    return when (manager.canAuthenticate(BIOMETRIC_STRONG)) {
        BIOMETRIC_SUCCESS ->
            BiometricStatus.READY
        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
            BiometricStatus.NOT_ENROLLED
        else ->
            BiometricStatus.UNAVAILABLE
    }
}



fun showBiometricPrompt(
    activity: FragmentActivity,
    cipher: Cipher,
    onSuccess: (Cipher) -> Unit,
    onError: (String) -> Unit
) {
    val executor = ContextCompat.getMainExecutor(activity)
    val callback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {

            val unlocked = result.cryptoObject?.cipher  //the cipher, now unlocked
            if(unlocked != null) {
                onSuccess(unlocked)
            } else {
                onError("No cipher returned")
            }
        }


        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            onError(errString.toString())
        }

        override fun onAuthenticationFailed() {
            // a finger was read but not recognized — dialog stays up for a retry, not fatal

        }
    }

    val prompt = BiometricPrompt(activity, executor, callback)

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Unlock AuthLab")
        .setSubtitle("Confirm your fingerprint to continue")
        .setNegativeButtonText("Cancel")
        .setAllowedAuthenticators(BIOMETRIC_STRONG)
        .build()

    prompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
}