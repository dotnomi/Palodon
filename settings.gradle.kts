pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    plugins {
        val kotlinVersion = "2.3.10"
        kotlin("jvm") version kotlinVersion
        kotlin("multiplatform") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        kotlin("plugin.allopen") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.compose") version kotlinVersion
        id("io.quarkus") version "3.17.0"
        id("com.varabyte.kobweb.application") version "0.24.0"
        id("com.varabyte.kobweb.library") version "0.24.0"
        id("com.varabyte.kobwebx.markdown") version "0.24.0"
        id("org.jetbrains.compose") version "1.10.0"
        id("com.android.application") version "8.13.2"
        id("com.android.library") version "8.13.2"
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "palodon"
include("server")
include("web-client")
include("native-client")
include("shared-lib")