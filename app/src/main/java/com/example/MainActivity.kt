package com.example

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.preferences.ThemeMode
import com.example.ui.screens.MainAppScreen
import com.example.ui.screens.security.AppLockScreen
import com.example.ui.theme.VendorListTheme
import com.example.ui.viewmodel.VendorViewModel

class MainActivity : FragmentActivity() {

    private var backgroundTimestamp: Long = 0L
    private var cachedViewModel: VendorViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: VendorViewModel = viewModel()
            cachedViewModel = viewModel

            val themeMode by viewModel.themeMode.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val useDark = when (themeMode) {
                ThemeMode.SYSTEM -> systemDark
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            val isAppLocked by viewModel.isAppLocked.collectAsState()
            val isAppLockEnabled by viewModel.isAppLockEnabled.collectAsState()
            val isScreenshotBlockEnabled by viewModel.isScreenshotBlockEnabled.collectAsState()
            val isDynamicColorEnabled by viewModel.isDynamicColorEnabled.collectAsState()

            // Handle FLAG_SECURE dynamically
            LaunchedEffect(isScreenshotBlockEnabled) {
                if (isScreenshotBlockEnabled) {
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_SECURE,
                        WindowManager.LayoutParams.FLAG_SECURE
                    )
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
            }

            // Trigger biometric prompt on lock state change
            LaunchedEffect(isAppLocked) {
                if (isAppLocked) {
                    showBiometricPrompt {
                        viewModel.unlockApp()
                    }
                }
            }

            VendorListTheme(darkTheme = useDark, dynamicColor = isDynamicColorEnabled) {
                if (isAppLocked && isAppLockEnabled) {
                    AppLockScreen(
                        onUnlockClick = {
                            showBiometricPrompt {
                                viewModel.unlockApp()
                            }
                        }
                    )
                } else {
                    MainAppScreen(viewModel = viewModel)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        backgroundTimestamp = System.currentTimeMillis()
    }

    override fun onStart() {
        super.onStart()
        val elapsed = System.currentTimeMillis() - backgroundTimestamp
        if (backgroundTimestamp > 0L && elapsed >= 30_000L) {
            cachedViewModel?.lockApp()
        }
    }

    private fun showBiometricPrompt(onSuccess: () -> Unit) {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // If device credentials/biometrics are unavailable or not configured, allow entry
                    if (errorCode == BiometricPrompt.ERROR_NO_BIOMETRICS ||
                        errorCode == BiometricPrompt.ERROR_HW_NOT_PRESENT ||
                        errorCode == BiometricPrompt.ERROR_HW_UNAVAILABLE
                    ) {
                        onSuccess()
                    }
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("احراز هویت وندوریاب")
            .setSubtitle("ورود امن به برنامه با اثر انگشت یا پین/رمز دستگاه")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (_: Exception) {
            // Graceful fallback if device has no lock screen credentials set up
            onSuccess()
        }
    }
}
