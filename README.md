# AuthLab

An Android reference app exploring modern authentication and on-device cryptography, built with Kotlin and Jetpack Compose. AuthLab implements a **biometric secret vault**: a note is encrypted with a hardware-backed key that can only be used after a live fingerprint authentication.

## What it demonstrates

- **Hardware-backed key storage** — the AES encryption key is generated inside the Android Keystore and never leaves secure hardware; the app can use it but cannot read it.
- **Biometric-gated cryptography** — the key is created with `setUserAuthenticationRequired(true)`, and a `BiometricPrompt.CryptoObject` binds fingerprint authentication directly to the cipher. Authentication isn't a boolean the app trusts; it's a cryptographic operation that cannot run without a live biometric.
- **Encrypt and decrypt loops** — both sealing and revealing a secret require a fresh fingerprint, so reading is as protected as writing.
- **Re-lock on background** — the vault re-locks in `onPause`, so a decrypted secret is never left visible in the app switcher or to someone who picks up the phone.
- **Persistence** — only ciphertext and its IV are written to storage; the plaintext never touches disk.

## Architecture

- **UI:** Jetpack Compose with unidirectional data flow — the ViewModel owns immutable state exposed as a `StateFlow`; the screen renders it and emits events upward.
- **State:** a `sealed interface` (`Idle`, `Loading`, `Locked`, `Revealed`, `Failure`) drives the screen with compiler-enforced exhaustive handling.
- **Pattern:** MVVM with `AndroidViewModel`, coroutines (`viewModelScope`), and Android's `BiometricPrompt` / Keystore APIs.

## Tech

Kotlin · Jetpack Compose · Material 3 · AndroidX Biometric · Android Keystore · Coroutines / StateFlow

## Status

Learning/reference project demonstrating passwordless and biometric authentication patterns on Android.