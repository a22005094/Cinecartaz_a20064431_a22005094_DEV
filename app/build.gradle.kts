plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // id("kotlin-parcelize")
    id("kotlin-kapt")
}

android {
    namespace = "pt.ulusofona.deisi.cm2223.g20064431_22005094"
    compileSdk = 31

    defaultConfig {
        applicationId = "pt.ulusofona.deisi.cm2223.g20064431_22005094"
        minSdk = 23
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

}

// Resolver problema recente de compatibilidade com o "Kapt".
//  Ver #1: https://stackoverflow.com/questions/76030538/android-agp-8-gradle-8-kotlin-1-8-causes-error-in-kapt
//  Ver #2: https://youtrack.jetbrains.com/issue/KT-55947/Unable-to-set-kapt-jvm-target-version
tasks.withType(type = org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask::class) {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
    implementation("androidx.test.espresso:espresso-contrib:3.5.1")
    // -----------
    // (Calculator app)
    //implementation("net.objecthunter:exp4j:0.4.4")
    // -----------
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.5")
    // -----------
    // Room
    implementation("androidx.room:room-runtime:2.4.2")
    kapt("androidx.room:room-compiler:2.4.2")
    // integração das coroutines do kotlin, é opcional mas vamos utilizar
    implementation("androidx.room:room-ktx:2.4.2")
    // -----------
    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    // -----------
    // Permissions manager
    implementation("com.github.fondesa:kpermissions:3.3.0")
    // -----------
    // GMaps & Location services
    implementation("com.google.android.gms:play-services-maps:18.0.2")
    implementation("com.google.android.gms:play-services-location:19.0.1")
    // -----------
    // Image management - Glide library
    implementation("com.github.bumptech.glide:glide:4.14.2")
    //annotationProcessor ("com.github.bumptech.glide:compiler:4.12.2")   // Additional dependency
    // -----------
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test:rules:1.4.0")
    // -----------

    // TODO - Para o OkHttp, rever demo final deste GitHub do LOTR: https://github.com/ULHT-CM-2021-22/LOTR-Characters
    //        MUITO INTERESSANTE! ver [OkHttp], [JsonObject], [Result @onSuccess + onFailure], [AlertDialog], [ProgressIndicator] (@ async)!
    //        Poderá também ser interessante isto (Fichas atualizadas): https://github.com/ULHT-CM-2022-23

    // needed to launch permissions window
    implementation("com.github.fondesa:kpermissions:3.3.0")

    // Fused location lib (higher precision than GPS only
    implementation ("com.google.android.gms:play-services-location:19.0.1")

    // Google keys usage
    implementation ("com.google.android.gms:play-services-maps:18.0.2")
}