import com.android.build.api.dsl.ApplicationBuildType
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
}

// Funkcja do bezpiecznego odczytu właściwości z local.properties
fun getLocalProperty(key: String): String {
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(localPropertiesFile.inputStream())
        return properties.getProperty(key) ?: ""
    }
    return ""
}

android {
    namespace = "com.example.myapp_428281_jj"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.myapp_428281_jj"
        minSdk = 35
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Pobierz token Mapbox - najpierw z local.properties, potem z gradle.properties
        val mapboxToken = getLocalProperty("MAPBOX_ACCESS_TOKEN")
            .ifEmpty { project.findProperty("MAPBOX_ACCESS_TOKEN") as? String ?: "" }

        buildConfigField("String", "MAPBOX_ACCESS_TOKEN", "\"$mapboxToken\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        // Dodaj debug build type z domyślnym tokenem dla developmentu
        debug {
            buildConfigField("String", "MAPBOX_ACCESS_TOKEN",
                "\"${getLocalProperty("MAPBOX_ACCESS_TOKEN").ifEmpty { "YOUR_DEV_TOKEN" }}\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    val room_version = "2.7.0-alpha11"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    implementation("org.osmdroid:osmdroid-android:6.1.20")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("com.mapbox.maps:android-ndk27:11.22.0")
    implementation("com.mapbox.extension:maps-compose-ndk27:11.22.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
