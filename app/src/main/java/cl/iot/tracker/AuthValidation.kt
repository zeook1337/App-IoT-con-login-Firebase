package cl.iot.tracker

object AuthValidation {
    fun error(email: String, password: String, registering: Boolean, confirmation: String): String? {
        if (!Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(email.trim())) {
            return "Ingresa un correo electrónico válido."
        }
        if (password.isEmpty()) return "Ingresa tu contraseña."
        if (registering && password.length < 6) return "Usa una contraseña de al menos 6 caracteres."
        if (registering && password != confirmation) return "Las contraseñas no coinciden."
        return null
    }
}
