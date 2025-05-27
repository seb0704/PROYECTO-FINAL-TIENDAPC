plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Keep this plugin, it simplifies Compose setup
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.tiendapcgamer"
    compileSdk = 35 // Keeping your current compileSdk

    defaultConfig {
        applicationId = "com.example.tiendapcgamer"
        minSdk = 24
        targetSdk = 35 // Keeping your current targetSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
    // REMOVED: composeOptions { kotlinCompilerExtensionVersion 'X.X.X' }
    // The 'kotlin.compose' plugin handles this automatically and correctly.
}

dependencies {
    // OkHttp & Retrofit - keep these versions as you specified
    implementation ("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)

    // Lottie - keep this version
    implementation("com.airbnb.android:lottie-compose:6.0.0")

    // Coil - Keep one instance of this
    implementation("io.coil-kt:coil-compose:2.4.0")

    // AndroidX & Compose - Use the BOM for versioning
    // This BOM will manage the versions for androidx.compose.* libraries
    implementation(platform(libs.androidx.compose.bom))

    // Core AndroidX libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose UI & Material3 (versions managed by the BOM)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended:<LATEST_VERSION>")

    implementation("androidx.navigation:navigation-compose:2.7.7") // Update this version!

    // Lifecycle and ViewModel
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.navigation.compose.jvmstubs) // This might be for older setups, keep if needed

    // Other specific libraries
    implementation(libs.generativeai)
    implementation(libs.transport.api)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.gson) // Gson for JSON parsing
    implementation(libs.coroutines.android)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.animation.core.lint) // Kotlin Coroutines for background tasks


    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}