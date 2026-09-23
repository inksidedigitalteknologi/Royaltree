package com.inkside.digital.ui.components

/**
 * Helper validasi password + strength.
 *
 * Policy:
 * - Minimal 8 karakter
 * - Minimal 1 huruf besar (A-Z)
 * - Minimal 1 huruf kecil (a-z)
 * - Minimal 1 angka (0-9)
 * - Simbol opsional (!@#$%^&*()_-+=)
 */
object PasswordValidator {

    private val UPPERCASE_REGEX = Regex("[A-Z]")
    private val LOWERCASE_REGEX = Regex("[a-z]")
    private val DIGIT_REGEX = Regex("[0-9]")
    private val SYMBOL_REGEX = Regex("[!@#\\$%^&*()_\\-+=]")

    data class ValidationResult(
        val minLength: Boolean,
        val hasUppercase: Boolean,
        val hasLowercase: Boolean,
        val hasDigit: Boolean,
        val hasSymbol: Boolean,
        val isValid: Boolean,
        val strength: Int,        // 0-4
        val strengthLabel: String, // "Lemah", "Sedang", "Kuat", "Sangat Kuat"
        val missing: List<String>
    )

    fun validate(password: String): ValidationResult {
        val minLength = password.length >= 8
        val hasUppercase = UPPERCASE_REGEX.containsMatchIn(password)
        val hasLowercase = LOWERCASE_REGEX.containsMatchIn(password)
        val hasDigit = DIGIT_REGEX.containsMatchIn(password)
        val hasSymbol = SYMBOL_REGEX.containsMatchIn(password)

        // Wajib: minLength + uppercase + lowercase + digit
        // Simbol opsional
        val isValid = minLength && hasUppercase && hasLowercase && hasDigit

        // Hitung kekuatan (0-4)
        var score = 0
        if (minLength) score++
        if (hasUppercase) score++
        if (hasLowercase) score++
        if (hasDigit) score++
        if (hasSymbol) score++

        val strength = when {
            score <= 2 -> 1
            score == 3 -> 2
            score == 4 -> 3
            else -> 4
        }

        val strengthLabel = when (strength) {
            1 -> "Lemah"
            2 -> "Sedang"
            3 -> "Kuat"
            4 -> "Sangat Kuat"
            else -> "Lemah"
        }

        val missing = mutableListOf<String>()
        if (!minLength) missing.add("Minimal 8 karakter")
        if (!hasUppercase) missing.add("Huruf besar (A-Z)")
        if (!hasLowercase) missing.add("Huruf kecil (a-z)")
        if (!hasDigit) missing.add("Angka (0-9)")

        return ValidationResult(
            minLength = minLength,
            hasUppercase = hasUppercase,
            hasLowercase = hasLowercase,
            hasDigit = hasDigit,
            hasSymbol = hasSymbol,
            isValid = isValid,
            strength = strength,
            strengthLabel = strengthLabel,
            missing = missing
        )
    }
}
