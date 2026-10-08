import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

val localConfig = Properties().apply {
    val configFile = rootProject.file("local.properties")
    if (configFile.exists()) configFile.inputStream().use { load(it) }
}
val comicVineKey = localConfig.getProperty("comicvine.api.key", "")
    .replace("\\", "\\\\").replace("\"", "\\\"")

android {
    namespace = "com.example.marveldefence"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.marveldefence"
        minSdk = 33
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "COMIC_VINE_KEY", "\"$comicVineKey\"")
        }
        release {
            buildConfigField("String", "COMIC_VINE_KEY", "\"\"")
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}
