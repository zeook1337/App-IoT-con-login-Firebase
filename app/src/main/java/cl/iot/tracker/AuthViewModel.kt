package cl.iot.tracker

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.ClearCredentialException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    var signedIn by mutableStateOf(auth.currentUser != null)
        private set
    var busy by mutableStateOf(false)
        private set
    var message by mutableStateOf<String?>(null)
        private set
    private val authListener = FirebaseAuth.AuthStateListener { signedIn = it.currentUser != null }

    init { auth.addAuthStateListener(authListener) }

    fun clearMessage() { message = null }

    fun submit(email: String, password: String, registering: Boolean, confirmation: String) {
        if (busy) return
        val validation = AuthValidation.error(email, password, registering, confirmation)
        if (validation != null) { message = validation; return }
        busy = true
        message = null
        viewModelScope.launch {
            try {
                if (registering) auth.createUserWithEmailAndPassword(email.trim(), password).await()
                else auth.signInWithEmailAndPassword(email.trim(), password).await()
            } catch (error: CancellationException) { throw error
            } catch (error: Exception) { message = friendlyError(error)
            } finally { busy = false }
        }
    }

    fun resetPassword(email: String) {
        if (busy) return
        if (AuthValidation.error(email, "unused", false, "") != null) {
            message = "Escribe tu correo para recuperar la contraseña."
            return
        }
        busy = true
        message = null
        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(email.trim()).await()
                message = "Si existe una cuenta con ese correo, recibirás un enlace para recuperar tu contraseña."
            } catch (error: CancellationException) { throw error
            } catch (error: Exception) { message = friendlyError(error)
            } finally { busy = false }
        }
    }

    fun startGoogle(): Boolean {
        if (busy) return false
        busy = true
        message = null
        return true
    }

    fun googleFailure(text: String?) { busy = false; message = text }

    fun completeGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
            } catch (error: CancellationException) { throw error
            } catch (error: Exception) { message = friendlyError(error)
            } finally { busy = false }
        }
    }

    fun signOut(context: Context) {
        if (busy) return
        busy = true
        message = null
        auth.signOut()
        val credentialManager = CredentialManager.create(context.applicationContext)
        viewModelScope.launch {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (error: CancellationException) {
                throw error
            } catch (_: ClearCredentialException) {
                // Firebase is already signed out even if the provider cannot clear its state.
                Log.w("AuthViewModel", "No se pudo limpiar el estado del proveedor de credenciales.")
            } finally {
                busy = false
            }
        }
    }

    override fun onCleared() { auth.removeAuthStateListener(authListener) }
}

private fun friendlyError(error: Exception): String = when (error) {
    is FirebaseNetworkException -> "No se pudo conectar. Revisa tu conexión a Internet."
    is FirebaseTooManyRequestsException -> "Demasiados intentos. Espera un momento y vuelve a intentar."
    is FirebaseAuthException -> when (error.errorCode) {
        "ERROR_INVALID_EMAIL" -> "Ingresa un correo electrónico válido."
        "ERROR_WEAK_PASSWORD" -> "La contraseña no cumple los requisitos de seguridad. Usa una más larga."
        "ERROR_EMAIL_ALREADY_IN_USE" -> "Este correo ya tiene una cuenta. Inicia sesión."
        "ERROR_USER_DISABLED" -> "Esta cuenta está deshabilitada."
        "ERROR_WRONG_PASSWORD", "ERROR_USER_NOT_FOUND", "ERROR_INVALID_CREDENTIAL", "ERROR_INVALID_LOGIN_CREDENTIALS" -> "Correo o contraseña incorrectos."
        "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> "Este correo ya usa otro método de acceso. Inicia sesión con ese método."
        "ERROR_OPERATION_NOT_ALLOWED" -> "Este método de acceso no está disponible por el momento."
        else -> "No se pudo completar la solicitud. Inténtalo nuevamente."
    }
    else -> "No se pudo completar la solicitud. Inténtalo nuevamente."
}
