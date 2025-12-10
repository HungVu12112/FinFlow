plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
    alias(libs.plugins.hilt.android)
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.devux.finflow"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.devux.finflow"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "2.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.glance)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    ///Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.hilt.navigation.fragment)

    ////room data
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    // Gson
    implementation(libs.gson)
    // HttpClient
    implementation(libs.logging.interceptor)
    //
    implementation(libs.androidx.multidex)
    //
    implementation (libs.play.services.auth)

    implementation(libs.smoothbottombar)

    implementation(libs.hilt.android)

    kapt(libs.hilt.compiler)
    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    /// Chart Pie
    implementation(libs.mpandroidchart)
}