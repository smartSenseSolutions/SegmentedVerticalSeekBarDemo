plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

buildscript {
    extra.apply {
        set("PUBLISH_GROUP_ID", "io.github.smartsensesolutions")
        set("PUBLISH_VERSION", "1.0.2")
        set("PUBLISH_ARTIFACT_ID", "SegmentedVerticalSeekBar")
        set("PUBLISH_DESCRIPTION", "Segmented Vertical SeekBar Android SDK")
        set("PUBLISH_URL", "https://github.com/smartSenseSolutions/SegmentedVerticalSeekBarDemo")
        set("PUBLISH_LICENSE_NAME", "Apache License")
        set("PUBLISH_LICENSE_URL", "https://github.com/smartSenseSolutions/SegmentedVerticalSeekBarDemo/blob/master/LICENSE")
        set("PUBLISH_DEVELOPER_ID", "sagarsmartsense")
        set("PUBLISH_DEVELOPER_NAME", "Sagar Maiyad")
        set("PUBLISH_DEVELOPER_EMAIL", "sagar.maiyad@smartsensesolutions.com")
        set("PUBLISH_SCM_CONNECTION", "scm:git:github.com/smartSenseSolutions/SegmentedVerticalSeekBarDemo.git")
        set("PUBLISH_SCM_DEVELOPER_CONNECTION", "scm:git:ssh://github.com/smartSenseSolutions/SegmentedVerticalSeekBarDemo.git")
        set("PUBLISH_SCM_URL", "https://github.com/smartSenseSolutions/SegmentedVerticalSeekBarDemo")
    }
}

apply {
    from("${rootProject.projectDir}/scripts/publish-module.gradle")
}

android {
    namespace = "com.ss.svs"
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }

    compileSdk = 34

    defaultConfig {
        minSdk = 21
    }

    buildTypes {
        getByName("release") {
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
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
}