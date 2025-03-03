plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.yinyuan.myworks"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.yinyuan.myworks"
        minSdk = 24
        targetSdk = 35
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
        viewBinding = true
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
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("androidx.camera:camera-core:1.1.0-alpha08")
    // If you want to use Camera2 extensions.
    implementation("androidx.camera:camera-camera2:1.1.0-alpha08")
    // CameraX Lifecycle Library
    implementation("androidx.camera:camera-lifecycle:1.1.0-alpha08")

    //other
    // If you to use the Camera View class
    implementation("androidx.camera:camera-view:1.0.0-alpha28")
    // If you to use Camera Extensions
    implementation("androidx.camera:camera-extensions:1.0.0-alpha28")

    //https://github.com/getActivity/ShapeView
    implementation("com.github.getActivity:ShapeView:9.5")
    implementation("com.github.getActivity:ShapeDrawable:3.3")

    // 动画解析库：https://github.com/airbnb/lottie-android
    // 动画资源：https://lottiefiles.com、https://icons8.com/animated-icons
    implementation("com.airbnb.android:lottie:4.1.0")

    //rxjava rxandroid
    implementation("io.reactivex.rxjava2:rxjava:2.2.19")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
}