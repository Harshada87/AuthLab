package com.example.authlab.ui


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    state: LoginUiState,
    onLock: (String) -> Unit,
    onReveal: () -> Unit,
    modifier: Modifier = Modifier

){
    var note by remember { mutableStateOf("") }
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        when (state){

            LoginUiState.Idle -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note") }
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = { onLock(note)}, enabled = note.isNotBlank()) {
                    Text("Lock with fingerprint")
                }
            }
            LoginUiState.Loading -> CircularProgressIndicator()
            is LoginUiState.Locked -> Column (horizontalAlignment = Alignment.CenterHorizontally){
                Text("Locked")
                Spacer(Modifier.height(8.dp))
                Text(state.preview, fontFamily = FontFamily.Monospace)
                Spacer(Modifier.height(8.dp))
                Button(onClick = onReveal) { Text("Reveal with fingerprint") }
            }

            is LoginUiState.Revealed -> Column (horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${state.note} ")

            }


            is LoginUiState.Failure -> Text(state.message)
            is LoginUiState.Success -> Text("WElcome, ${state.user.name}")

        }
    }


}

@Composable
fun LoginForm() {
    Column(modifier = Modifier.padding(24.dp)) {
        Text("AuthLab")
        OutlinedTextField(value = "", onValueChange = {}, label = { Text("Email") })
        Button(onClick = {}) { Text("Sign in") }
    }
}
