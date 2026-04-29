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
        versionCode = 4
        versionName = "2.0"
    }

    buildToolsVersion = "33.0.2"

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility =
            JavaVersion.VERSION_17
        targetCompatibility =
            JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget="17"
    }
}

dependencies {

implementation(
"androidx.core:core-ktx:1.10.1"
)

implementation(
"androidx.appcompat:appcompat:1.6.1"
)

implementation(
"com.google.android.material:material:1.9.0"
)

}
