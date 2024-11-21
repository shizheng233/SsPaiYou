import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(common.plugins.ksp)
    alias(common.plugins.tools.hilt)
    kotlin("plugin.serialization") version "2.0.21"
}

android {
    namespace = "com.shihcheeng.sspaiyou"
    compileSdk = 35

    defaultConfig {
        val file = project.rootProject.file("version.code.properties")
        val codeProperties = Properties()
        codeProperties.load(file.inputStream())
        val codeOfVersion = codeProperties.getProperty("VERSION_CODE").toInt() + 1
        val nameOfVersion = codeProperties.getProperty("VERSION_NAME")
        codeProperties["VERSION_CODE"] = codeOfVersion.toString()
        applicationId = "com.shihcheeng.sspaiyou"
        minSdk = 29
        targetSdk = 35
        versionCode = codeOfVersion
        versionName = nameOfVersion
        codeProperties.store(file.writer(), null)
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
        debug {
            applicationIdSuffix = ".debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(common.tools.coil)
    implementation(common.tools.coil.compose)
    implementation(common.tools.retrofit)
    implementation(platform(common.tools.okhttp.bom))
    implementation(common.tools.okhttp)
    implementation(common.tools.hilt)
    implementation(common.tools.hilt.navigation.compose)
    implementation(common.androidx.nav)
    implementation(common.tools.retrofit.ktx.serialization)
    ksp(common.tools.hilt.complie)
    implementation(libs.kotlinx.serialization.json)
    implementation(common.tools.motion)
    implementation(common.androidx.paging.runtime)
    implementation(common.androidx.paging.compose)
    implementation(common.tools.okhttp.logging.interceptor)
    implementation(libs.jsoup)
    implementation(libs.richeditor.compose)
    implementation(libs.compose.webview)
    implementation(libs.agentweb.core)
    implementation(common.tools.materialKolor)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}