package cl.iot.tracker

import org.junit.Assert.*
import org.junit.Test

class AuthValidationTest {
    @Test fun rejectsMalformedAndEmptyEmail() {
        listOf("", "sin-arroba", "a@", "a b@example.com").forEach {
            assertNotNull(AuthValidation.error(it, "123456", false, ""))
        }
    }
    @Test fun trimsEmailAndAllowsExistingShortPasswordOnLogin() {
        assertNull(AuthValidation.error(" usuario@example.com ", "123", false, ""))
    }
    @Test fun registrationRequiresLengthAndMatchingConfirmation() {
        assertNotNull(AuthValidation.error("a@example.com", "123", true, "123"))
        assertNotNull(AuthValidation.error("a@example.com", "123456", true, "654321"))
        assertNull(AuthValidation.error("a@example.com", "123456", true, "123456"))
    }
    @Test fun rejectsEmptyPassword() {
        assertNotNull(AuthValidation.error("a@example.com", "", false, ""))
    }
}
