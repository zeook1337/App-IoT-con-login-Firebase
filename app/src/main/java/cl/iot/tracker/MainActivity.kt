package cl.iot.tracker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(
                primary = Color(0xFF166B57), onPrimary = Color.White,
                background = Color(0xFFF5F8F6), surface = Color.White,
                onBackground = Color(0xFF172C25), onSurface = Color(0xFF172C25)
            )) {
                val model: AuthViewModel = viewModel()
                val context = LocalContext.current
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    if (model.signedIn) {
                        Box(Modifier.fillMaxSize().safeDrawingPadding(), contentAlignment = Alignment.Center) {
                            Text("Página en desarrollo", fontSize = 24.sp, textAlign = TextAlign.Center,
                                modifier = Modifier.padding(24.dp))
                            TextButton(
                                onClick = { model.signOut(context) },
                                enabled = !model.busy,
                                modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
                            ) {
                                Text("Cerrar sesión")
                            }
                        }
                    } else AuthScreen(model)
                }
            }
        }
    }
}

@Composable
private fun AuthScreen(model: AuthViewModel) {
    var email by rememberSaveable { mutableStateOf("") }
    // Passwords stay only in memory and are never written to saved instance state.
    var password by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }
    var registering by rememberSaveable { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    val focus = LocalFocusManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val submit = {
        focus.clearFocus()
        model.submit(email, password, registering, confirmation)
    }

    Column(
        Modifier.fillMaxSize().safeDrawingPadding().imePadding()
            .verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(color = MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.large) {
            Text("IoT", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 15.dp))
        }
        Spacer(Modifier.height(24.dp))
        Text("Acceso IoT", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(if (registering) "Crea tu cuenta para comenzar" else "Bienvenido. Inicia sesión para continuar.",
            color = Color(0xFF566B62), textAlign = TextAlign.Center)
        Spacer(Modifier.height(28.dp))
        Card(Modifier.widthIn(max = 440.dp).fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(if (registering) "Crear cuenta" else "Iniciar sesión", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(email, { email = it; model.clearMessage() },
                    modifier = Modifier.fillMaxWidth(), label = { Text("Correo electrónico") },
                    singleLine = true, enabled = !model.busy,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next))
                OutlinedTextField(password, { password = it; model.clearMessage() },
                    modifier = Modifier.fillMaxWidth(), label = { Text("Contraseña") },
                    singleLine = true, enabled = !model.busy,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(onClick = { passwordVisible = !passwordVisible }, enabled = !model.busy,
                            contentPadding = PaddingValues(horizontal = 8.dp)) {
                            Text(if (passwordVisible) "Ocultar" else "Mostrar", fontSize = 12.sp)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password,
                        imeAction = if (registering) ImeAction.Next else ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { submit() }))
                if (registering) {
                    OutlinedTextField(confirmation, { confirmation = it; model.clearMessage() },
                        modifier = Modifier.fillMaxWidth(), label = { Text("Confirmar contraseña") },
                        singleLine = true, enabled = !model.busy,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { submit() }))
                    Text("Usa al menos 6 caracteres.", fontSize = 12.sp, color = Color(0xFF566B62))
                } else {
                    TextButton(onClick = { focus.clearFocus(); model.resetPassword(email) },
                        enabled = !model.busy, modifier = Modifier.align(Alignment.End)) {
                        Text("¿Olvidaste tu contraseña?")
                    }
                }
                model.message?.let {
                    Text(it, color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth().background(Color(0xFFF0F4F1), MaterialTheme.shapes.small).padding(12.dp))
                }
                Button(onClick = { submit() }, enabled = !model.busy,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
                    if (model.busy) {
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary)
                        Spacer(Modifier.width(10.dp))
                        Text("Conectando…")
                    } else Text(if (registering) "Crear cuenta" else "Entrar")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(Modifier.weight(1f))
                    Text("o", Modifier.padding(horizontal = 12.dp), color = Color(0xFF566B62))
                    HorizontalDivider(Modifier.weight(1f))
                }
                OutlinedButton(onClick = {
                    focus.clearFocus()
                    if (model.startGoogle()) scope.launch {
                        try {
                            // The OAuth WEB client ID is generated from app/google-services.json.
                            val clientId = googleClientId(context)
                            if (clientId.isBlank()) {
                                model.googleFailure("El acceso con Google no está disponible por el momento.")
                            } else {
                                val option = GetSignInWithGoogleOption.Builder(clientId).build()
                                val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
                                val credential = CredentialManager.create(context).getCredential(context, request).credential
                                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                    model.completeGoogle(GoogleIdTokenCredential.createFrom(credential.data).idToken)
                                } else model.googleFailure("No se pudo verificar la cuenta de Google.")
                            }
                        } catch (_: GetCredentialCancellationException) { model.googleFailure(null)
                        } catch (_: NoCredentialException) {
                            model.googleFailure("Agrega una cuenta de Google al dispositivo y vuelve a intentar.")
                        } catch (error: CancellationException) { model.googleFailure(null); throw error
                        } catch (_: Exception) { model.googleFailure("No se pudo iniciar sesión con Google. Inténtalo nuevamente.") }
                    }
                }, enabled = !model.busy, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
                    Text("Continuar con Google")
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = {
            registering = !registering
            password = ""
            confirmation = ""
            passwordVisible = false
            model.clearMessage()
        }, enabled = !model.busy) {
            Text(if (registering) "¿Ya tienes cuenta? Inicia sesión" else "¿No tienes cuenta? Regístrate",
                textAlign = TextAlign.Center)
        }
    }
}

// This optional generated resource is absent until Google OAuth is enabled in Firebase.
@SuppressLint("DiscouragedApi")
private fun googleClientId(context: Context): String {
    val id = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
    return if (id != 0) context.getString(id) else ""
}
