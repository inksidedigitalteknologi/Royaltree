import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy
import java.util.Properties

// Load local.properties untuk secret keys
val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        localFile.inputStream().use { load(it) }
    }
}

// Load keystore.properties untuk signing config (jangan commit!)
val keystoreProperties = Properties().apply {
    val keystoreFile = rootProject.file("keystore.properties")
    if (keystoreFile.exists()) {
        keystoreFile.inputStream().use { load(it) }
    }
}

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
  alias(libs.plugins.google.services)
  alias(libs.plugins.firebase.crashlytics)
}

// App version — single source of truth untuk defaultConfig + APK filename
// ============ VERSIONING ============
// Format: V1.xx → 1.00, 1.01, 1.02, ..., 1.10, ..., 2.00
// versionCode = major * 100 + minor (1.00 = 100, 1.01 = 101)
val appVersionName = "1.12"
val appVersionCode = 112

android {
  namespace = "com.inkside.digital"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.inkside.digital"
    minSdk = 24
    targetSdk = 36
    versionCode = appVersionCode
    versionName = appVersionName

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    // ✅ SECURITY: API key dari local.properties (tidak hardcode)
    buildConfigField(
      "String",
      "ROYALTREE_API_KEY",
      "\"${localProperties["royaltree.api.key"] ?: ""}\""
    )

  }

  // ABI Split config (DISABLED untuk test)
  // splits {
  //   abi {
  //     isEnable = true
  //     reset()
  //     include("arm64-v8a", "armeabi-v7a")
  //     isUniversalApk = false
  //   }
  // }

  signingConfigs {
    create("release") {
      val keystorePath = keystoreProperties.getProperty("storeFile")
        ?: System.getenv("KEYSTORE_PATH")
        ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = keystoreProperties.getProperty("storePassword")
        ?: System.getenv("STORE_PASSWORD")
      keyAlias = keystoreProperties.getProperty("keyAlias")
        ?: "upload"
      keyPassword = keystoreProperties.getProperty("keyPassword")
        ?: System.getenv("KEY_PASSWORD")
    }
    create("debugConfig") {
      storeFile = file(System.getProperty("user.home") + "/.android/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug { signingConfig = signingConfigs.getByName("debugConfig") }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  kotlin {
    compilerOptions {
      freeCompilerArgs.add("-Xannotation-default-target=param-property")
    }
  }


  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

// ============ Custom APK name (AGP 9 API) ============
// Output: royaltree-v1.0-debug.apk / royaltree-v1.0-release.apk
androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            val version = appVersionName
            val buildType = variant.buildType ?: "release"
            output.outputFileName.set("royaltree-v${version}-${buildType}.apk")
        }
    }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
  ignoreList.add("FIREBASE_APPCHECK_DEBUG_TOKEN")
}

googleServices { missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN }

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.camera.camera2)
  implementation(libs.androidx.camera.core)
  implementation(libs.androidx.camera.lifecycle)
  implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.billing)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.work.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  // implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.coil.compose)
  implementation(libs.converter.moshi)
  implementation(libs.firebase.ai)
  // Uncomment to use Firestore:
  // implementation(libs.firebase.firestore)

  // Firebase Auth + Google Sign-In (Credential Manager)
  implementation(libs.firebase.auth)
  implementation(libs.firebase.crashlytics)
  implementation(libs.firebase.analytics)
  implementation(libs.firebase.messaging)
  implementation(libs.androidx.credentials)
  implementation(libs.androidx.credentials.play.services)
  implementation(libs.googleid)
  implementation(libs.firebase.appcheck.recaptcha)
  implementation(libs.firebase.appcheck.debug)
  // AdMob Rewarded Video
  implementation("com.google.android.gms:play-services-ads:25.4.0")
  // Glance — App Widget dengan Jetpack Compose
  implementation(libs.androidx.glance.appwidget)
  implementation(libs.androidx.glance.material3)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  implementation(libs.play.services.location)
  implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  // "ksp"(libs.moshi.kotlin.codegen)
}
