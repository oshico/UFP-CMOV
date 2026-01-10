plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "edu.ufp.pamo.project"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "edu.ufp.pamo.project"
        minSdk = 33
        targetSdk = 36
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
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime.livedata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    //============================= nanohttpd =============================
    implementation(libs.nanohttpd)

    //============================= ViewModel and LiveData libs =============================
    // New usage (<dependency_name> must be defined inside libs.versions.toml):
    // IF <dependency-name> = "androidx-lifecycle-viewmodel-savedstate"
    // THEN use implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    // Kotlin use/manage lifecycle-viewmodel, lifecycle.livedata and lifecycle.savedstate
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.savedstate)
    //Some UI AndroidX libraries use this lightweight import for Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // Kotlin use lifecycle-reactivestreams-ktx
    implementation(libs.androidx.lifecycle.reactivestreams.ktx)
    //Support RecyclerView - for listing Consumers from Room DB
    implementation (libs.androidx.recyclerview)

    //============================= Room DB libs and tools =============================
    // Fix Duplicate class problem when using Room with Hilt and KSP
    // NB: also add line *android.enableJetifier=true* into file *gradle.properties*
    implementation(platform(libs.org.jetbrains.kotlin.bom))
    // New usage (<dependency_name> must be defined inside libs.versions.toml):
    // IF <dependency-name> = "androidx-room-runtime" THEN use implementation(libs.androidx.room.runtime)
    //Use Room runtime
    implementation(libs.androidx.room.runtime)
    // Use Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)
    // Support for managing Activities navigation with Intents
    implementation(libs.androidx.activity)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    //Use Kotlin Symbolic Processing (KSP) -> substitutes Kotlin Annotation Processing Tool (KAPT)
    // Some libraries that use KSP are Room and Hilt dependency injection, hence we need to add
    // KSP compiler dependencies for room compiler, hilt lib and hilt compiler
    ksp(libs.android.room.compiler)
    ksp(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    // IF <dependency-name> = 'hilt-android' THEN use kps(libs.hilt.android)
    implementation(libs.hilt.android)
    // optional - RxJava2 support for Room
    implementation(libs.room.rxjava2)
    // optional - RxJava3 support for Room
    implementation(libs.room.rxjava3)
    // optional - Guava support for Room, including Optional and ListenableFuture
    implementation(libs.room.guava)
    implementation(libs.room.paging)

    //============================= Services, Work and HTTP libs =============================
    //Support for HTTP requests with Volley and OkHttp
    implementation (libs.volley)
    implementation (libs.play.services.cronet)
    implementation (libs.okhttp)
    implementation (libs.conscrypt.android)
    // Worker threads stuff - WorkManager
    implementation(libs.androidx.work.runtime)
    // Kotlin extensions (ktx) + coroutines
    implementation(libs.androidx.work.runtime.ktx)
    // optional - RxJava2 support
    implementation(libs.androidx.work.rxjava2)
    // optional - GCMNetworkManager support
    implementation(libs.androidx.work.gcm)
    // optional - Multiprocess supportdata_extraction_rules.xml
    implementation (libs.androidx.work.multiprocess)

    //================================= Maps and Location helpers =================================
    // Support for integrating Maps and Location
    implementation(libs.com.google.android.gms.play.services.maps)
    implementation (libs.play.services.location)


    //================================= Unit Test libs =================================
    // Dependency configuration testImplementation():
    //   - specify dependencies for local (unit) tests, i.e. executed in JVM (no dev/emulator);
    //   - test business logic / utility methods / non-Android-specific code;
    //   - used with frameworks such as JUnit, Mockito;
    //   - dependencies only available in test source set (src/test/)
    // JUnit framework
    testImplementation(libs.junit)
    // Unit Test helpers for LiveData
    testImplementation (libs.androidx.core.testing)
    // AndroidX Test - Core library
    testImplementation (libs.androidx.test.core)
    // Tests with Room stuff
    testImplementation (libs.androidx.room.testing)

    //============================= Android Instrumented Test libs =============================
    // Dependency configuration androidTestImplementation():
    //   - specify dependencies for instrumented (UI/Integration) tests, i.e.  executed on dev/emulator;
    //   - test Activities or UI interactions;
    //   - used with frameworks such as Espresso, UI Automator;
    //   - dependencies available in androidTest source set (src/androidTest/)
    // AndroidX Test - JUnit extensions
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation (libs.androidx.core.testing)
    kspAndroidTest(libs.hilt.android.compiler)
    // Android Test helpers for HTTP workers
    androidTestImplementation(libs.androidx.work.testing)
    // Android Tests with Espresso stuff
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation (libs.androidx.espresso.intents)
    androidTestImplementation (libs.androidx.espresso.web)
    // Unit Test with Mock components
    testImplementation (libs.mockito.core)
}