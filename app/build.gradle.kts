plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.rambo.ramcryptr"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.rambo.ramcryptr"
        minSdk = 24
        targetSdk = 33
        versionCode = 7
        versionName = "3.1.1"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // 🔥 Force same Kotlin version
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.20")

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
}

configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlin:kotlin-stdlib:1.7.20")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.7.20")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.20")
    }
}
