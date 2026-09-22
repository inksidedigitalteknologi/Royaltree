package com.inkside.digital.ui.screens.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.inkside.digital.data.auth.AuthManager
import com.inkside.digital.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * State untuk navigasi auth
 */
enum class AuthScreen {
    SPLASH,
    LOGIN,
    REGISTER,
    VERIFY_EMAIL,
    PROFILE_SETUP,
    HOME
}

/**
 * AuthViewModel: handle logika autentikasi Firebase + sync ke backend
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "AuthViewModel"

    private val authManager = AuthManager(application)
    private val authRepository = AuthRepository()

    // ============ STATE ============

    private val _currentScreen = MutableStateFlow(AuthScreen.SPLASH)
    val currentScreen: StateFlow<AuthScreen> = _currentScreen.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _needsProfileSetup = MutableStateFlow(false)
    val needsProfileSetup: StateFlow<Boolean> = _needsProfileSetup.asStateFlow()

    // ============ INIT ============

    init {
        checkInitialAuthState()
    }

    /**
     * Cek saat app dibuka: user sudah login atau belum?
     */
    private fun checkInitialAuthState() {
        val user = authManager.currentUser
        if (user == null) {
            _currentScreen.value = AuthScreen.LOGIN
            return
        }

        _currentUser.value = user

        // Cek email verified
        if (!user.isEmailVerified) {
            _currentScreen.value = AuthScreen.VERIFY_EMAIL
            return
        }

        // Sync ke backend & cek apakah profil sudah ada
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = authRepository.getMyProfile()
                if (result.isSuccess) {
                    _currentScreen.value = AuthScreen.HOME
                } else {
                    // Profil belum ada -> minta setup
                    _needsProfileSetup.value = true
                    _currentScreen.value = AuthScreen.PROFILE_SETUP
                }
            } catch (e: Exception) {
                Log.e(TAG, "checkInitialAuthState error: ${e.message}")
                _currentScreen.value = AuthScreen.LOGIN
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ============ LOGIN ============

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = authManager.loginWithEmail(email, password)
            if (result.isSuccess) {
                val user = result.getOrThrow()
                _currentUser.value = user

                if (!user.isEmailVerified) {
                    _currentScreen.value = AuthScreen.VERIFY_EMAIL
                } else {
                    syncUserAndNavigate()
                }
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Login gagal"
            }
            _isLoading.value = false
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = authManager.loginWithGoogleIdToken(idToken)
            if (result.isSuccess) {
                val user = result.getOrThrow()
                _currentUser.value = user

                // Google selalu email verified
                syncUserAndNavigate()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Google login gagal"
            }
            _isLoading.value = false
        }
    }

    // ============ REGISTER ============

    fun registerWithEmail(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = authManager.registerWithEmail(email, password, displayName)
            if (result.isSuccess) {
                val user = result.getOrThrow()
                _currentUser.value = user
                _successMessage.value = "Registrasi berhasil. Cek email untuk verifikasi."
                _currentScreen.value = AuthScreen.VERIFY_EMAIL
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Registrasi gagal"
            }
            _isLoading.value = false
        }
    }

    // ============ VERIFIKASI EMAIL ============

    fun resendVerificationEmail() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = authManager.resendVerificationEmail()
            if (result.isSuccess) {
                _successMessage.value = "Email verifikasi terkirim ulang."
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Gagal kirim email"
            }
            _isLoading.value = false
        }
    }

    fun checkEmailVerified() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = authManager.reloadUser()
            if (result.isSuccess) {
                val user = result.getOrThrow()
                _currentUser.value = user

                if (user.isEmailVerified) {
                    syncUserAndNavigate()
                } else {
                    _errorMessage.value = "Email belum diverifikasi. Cek inbox kamu."
                }
            } else {
                _errorMessage.value = "Gagal cek status verifikasi."
            }
            _isLoading.value = false
        }
    }

    // ============ PROFILE SETUP ============

    fun saveProfileSetup(
        name: String,
        phone: String,
        city: String,
        referralCode: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val token = authManager.getIdToken() ?: run {
                _errorMessage.value = "Sesi habis. Login ulang."
                _isLoading.value = false
                return@launch
            }

            val result = authRepository.syncUserToBackend(
                firebaseIdToken = token,
                name = name,
                phone = phone,
                city = city,
                referralCode = referralCode
            )

            if (result.isSuccess) {
                _successMessage.value = "Profil berhasil disimpan!"
                _needsProfileSetup.value = false
                _currentScreen.value = AuthScreen.HOME
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Gagal simpan profil"
            }
            _isLoading.value = false
        }
    }

    // ============ SYNC & NAVIGATE ============

    private suspend fun syncUserAndNavigate() {
        try {
            val token = authManager.getIdToken()
            if (token == null) {
                _currentScreen.value = AuthScreen.LOGIN
                return
            }

            // Cek apakah profil sudah ada di backend
            val profileResult = authRepository.getMyProfile()
            if (profileResult.isSuccess) {
                _currentScreen.value = AuthScreen.HOME
            } else {
                // Belum ada -> minta setup
                _needsProfileSetup.value = true
                _currentScreen.value = AuthScreen.PROFILE_SETUP
            }
        } catch (e: Exception) {
            Log.e(TAG, "syncUserAndNavigate error: ${e.message}")
            _currentScreen.value = AuthScreen.LOGIN
        }
    }

    // ============ LOGOUT ============

    fun logout() {
        authManager.logout()
        _currentUser.value = null
        _currentScreen.value = AuthScreen.LOGIN
        _errorMessage.value = null
        _successMessage.value = null
    }

    // ============ CLEAR MESSAGES ============

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSuccess() {
        _successMessage.value = null
    }
}
