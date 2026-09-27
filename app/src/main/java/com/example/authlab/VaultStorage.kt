package com.example.authlab

import android.content.Context
import android.util.Base64


private const val PREFS = "authlab_vault"
private const val KEY_CT = "ciphertext"
private const val KEY_IV = "iv"

fun saveEncrypted(context: Context, data: EncryptedData){
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
        .putString(KEY_CT, Base64.encodeToString(data.ciphertext, Base64.NO_WRAP))
        .putString(KEY_IV, Base64.encodeToString(data.iv, Base64.NO_WRAP))
        .apply()
}

fun loadEncrypted(context: Context) : EncryptedData? {
    val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    val ct = prefs.getString(KEY_CT, null) ?: return null
    val iv = prefs.getString(KEY_IV, null)?: return null
    return EncryptedData(
        ciphertext = Base64.decode(ct, Base64.NO_WRAP),
        iv = Base64.decode(iv, Base64.NO_WRAP)
    )
}