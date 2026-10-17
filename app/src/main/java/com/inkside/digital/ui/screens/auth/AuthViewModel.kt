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
    ONBOARDING,
    SPLASH,
    LOGIN,
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

        // Google Sign-In: email otomatis verified, langsung sync ke backend
        // Sync ke backend & cek apakah profil sudah ada
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = authRepository.getMyProfile()
                if (result.isSuccess) {
                    // Tampil splash dulu, baru HOME setelah selesai
                    _currentScreen.value = AuthScreen.SPLASH
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

    fun loginWithGoogle(idToken: String, marketingOptIn: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = authManager.loginWithGoogleIdToken(idToken)
            if (result.isSuccess) {
                val user = result.getOrThrow()
                _currentUser.value = user

                // Simpan marketing opt-in ke backend (non-blocking)
                viewModelScope.launch {
                    val marketingResult = authRepository.updateMarketingOptIn(marketingOptIn)
                    if (marketingResult.isFailure) {
                        Log.w(TAG, "Marketing opt-in save failed (non-fatal): ${marketingResult.exceptionOrNull()?.message}")
                    }
                }

                // Google selalu email verified
                syncUserAndNavigate()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Google login gagal"
            }
            _isLoading.value = false
        }
    }

    // ============ VERIFIKASI EMAIL ============

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

    // ============ NAVIGASI ============

    fun navigateToLogin() {
        _errorMessage.value = null
        _successMessage.value = null
        _currentScreen.value = AuthScreen.LOGIN
    }

    /**
     * navigateTo — method generik untuk navigate ke screen apa-apa.
     * Dipakai oleh SplashSequenceScreen untuk navigate ke HOME bila preload complete.
     */
    fun navigateTo(screen: AuthScreen) {
        _errorMessage.value = null
        _successMessage.value = null
        _currentScreen.value = screen
    }

    // ============ RESET PASSWORD ============
}
