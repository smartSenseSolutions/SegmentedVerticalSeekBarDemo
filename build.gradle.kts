// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.1" apply false
    id("com.android.library") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0" apply true
}

apply {
    from("${rootDir}/scripts/publish-root.gradle")
}

tasks {
    register("clean", Delete::class) {
        delete(rootProject.buildDir)
    }
}